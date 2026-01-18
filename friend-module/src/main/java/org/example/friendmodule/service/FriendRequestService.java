package org.example.friendmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.RequestData;
import org.example.common.dto.friend.FriendNotificationDto;
import org.example.common.dto.friend.FriendNotificationResponseDto;
import org.example.common.dto.user.UserDto;
import org.example.friendmodule.dto.FriendRequestDto;
import org.example.friendmodule.entity.FriendEntity;
import org.example.friendmodule.entity.FriendRequestEntity;
import org.example.friendmodule.entity.FriendRequestStatus;
import org.example.friendmodule.entity.ResponseFriendRequest;
import org.example.friendmodule.mapper.FriendRequestMapper;
import org.example.friendmodule.repository.FriendRequestRepository;
import org.example.friendmodule.util.FriendRequestSpecification;
import org.example.httpcore.httpCore.IHttpCore;
import org.example.httpcore.retryPolicy.FixedDelayRetryPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendService friendService;
    private final FriendRequestMapper friendRequestMapper;
    private final FriendNotificationService friendNotificationService;
    private final FixedDelayRetryPolicy defaultRetryPolicy;
    private final IHttpCore httpCore;

    @SneakyThrows
    public <T> ResponseEntity<T> retryExecute(RetryCallback<ResponseEntity<T>, Throwable> retryCallback, String name) {
        return defaultRetryPolicy.getRetryTemplate().execute(context -> {
            context.setAttribute("имя метода", name);
            log.info("[INFO RETRY] Попытка выполнить метод {}", name);
            try {
                ResponseEntity<T> response = retryCallback.doWithRetry(context);
                if (response.getStatusCode().is4xxClientError()) {
                    throw new IllegalArgumentException("Не удалось выполнить метод " + name);
                }
                if (isNull(response.getBody())) {
                    throw new IllegalArgumentException("Не удалось получить тело ответа " + response);
                }

                return response;
            } catch (Throwable throwable) {
                log.error("[ERROR RETRY] Не удалось выполнить метод {}", name, throwable);
                throw throwable;
            }
        });
    }

    @Transactional
    public FriendRequestDto addFriendRequest(Long requesterId, Long addresseeId) {

        ResponseEntity<UserDto> addFriendRequest = retryExecute(context -> {
            RequestData requestData = new RequestData(
                    "http://localhost:8080/api/v1/social/users/post",
                    null
            );

            String name = (String) context.getAttribute("имя метода");
            log.info("[INFO RETRY] Выполняем метод: {}", name);

            ResponseEntity<UserDto> userDtoResponseEntity =
                    httpCore.get(requestData, null, UserDto.class);
            log.info("[INFO RETRY] Получен ответ от сервера: {}", userDtoResponseEntity);
            return userDtoResponseEntity;
        }, "addFriendRequest");
        UserDto body = addFriendRequest.getBody();
        log.info("[INFO RETRY] Получен тело ответа: {}", body);

        if (!isNull(requesterId)) {
            throw new IllegalArgumentException("Не указан запрос");
        }

        if (requesterId.equals(addresseeId)) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }
        friendRequestRepository.findBetweenUsers(
                requesterId,
                addresseeId
        ).ifPresent(existing -> {
            throw new IllegalStateException(
                    "Связь между пользователями уже существует"
            );
        });

        FriendRequestEntity friendRequestEntity = new FriendRequestEntity();
        friendRequestEntity.setRequesterId(requesterId);
        friendRequestEntity.setAddresseeId(addresseeId);
        friendRequestEntity.setStatus(FriendRequestStatus.PENDING);
        friendRequestEntity.setRespondedAt(LocalDateTime.now());

        FriendNotificationDto requestNotificationDto = new FriendNotificationDto(
                addresseeId, requesterId
        );
        friendNotificationService.sendFriendRequestNotification(requestNotificationDto);

        return friendRequestMapper.toDto(friendRequestRepository.save(friendRequestEntity));
    }

    @Transactional
    public void deleteRequestFriend(Long requesterId, Long addresseeId) {

        FriendRequestEntity requestEntity = friendRequestRepository
                .findByRequesterIdAndAddresseeId(requesterId, addresseeId);

        if (!requestEntity.getRequesterId().equals(requesterId)) {
            throw new AccessDeniedException("Только автор заявки может её удалить!");
        }

        if (requestEntity.getStatus().equals(FriendRequestStatus.ACCEPTED)) {
            throw new IllegalStateException("Заявка уже принята. Требуется удалить из друзей");
        }

        friendRequestRepository.deleteById(requestEntity.getId());
    }

    @Transactional(readOnly = true)
    public List<FriendRequestDto> getRequesterRequestSpecification(
            Long currentUserId,
            FriendRequestStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<FriendRequestEntity> spec = FriendRequestSpecification.requestsByStatus(currentUserId, status);
        Page<FriendRequestEntity> requestsPage = friendRequestRepository.findAll(spec, pageable);

        return requestsPage.getContent()
                .stream()
                .map(friendRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FriendRequestDto> getUserRequestsFromFriends(Long currentUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<FriendRequestEntity> spec = FriendRequestSpecification
                .incomingPendingRequests(currentUserId);

        Page<FriendRequestEntity> requestsPage = friendRequestRepository
                .findAll(spec, pageable);

        return requestsPage.getContent()
                .stream()
                .map(friendRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public String responseToRequest(Long requestId, ResponseFriendRequest status, Long userId) {
        if (isNull(status)) {
            throw new IllegalArgumentException("Новый статус не может быть Null!");
        }

        FriendRequestEntity requestEntity = friendRequestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException("Запрос не был найден!"));

        if (!requestEntity.getAddresseeId().equals(userId)) {
            throw new AccessDeniedException("Только владелец может изменять свои входящие заявки");
        }

        if (status.equals(ResponseFriendRequest.ACCEPTED)) {
            requestEntity.setStatus(FriendRequestStatus.ACCEPTED);

            FriendEntity friendEntity = new FriendEntity();
            friendEntity.setUserId1(userId);
            friendEntity.setUserId2(requestEntity.getRequesterId());
            friendService.createFriendship(friendEntity);

            // уведомление принявшему: вы теперь друзья
            FriendNotificationResponseDto responseDtoForRequester = new FriendNotificationResponseDto(
                    requestId,
                    requestEntity.getRequesterId(),
                    status.toString()
            );
            friendNotificationService.responseToRequestNotification(responseDtoForRequester);

            // уведомление отправителю: заявка принята
            FriendNotificationResponseDto responseDtoForAddressee = new FriendNotificationResponseDto(
                    requestId,
                    requestEntity.getAddresseeId(),
                    "NOW_FRIENDS"
            );
            friendNotificationService.responseToRequestNotification(responseDtoForAddressee);
        } else if (status.equals(ResponseFriendRequest.REJECTED)) {
            requestEntity.setStatus(FriendRequestStatus.REJECTED);
            // уведомление отправителю: заявка отказано
            FriendNotificationResponseDto responseDtoForRequester = new FriendNotificationResponseDto(
                    requestId,
                    requestEntity.getRequesterId(),
                    status.toString()
            );
            friendNotificationService.responseToRequestNotification(responseDtoForRequester);
        }

        return requestEntity.getStatus().toString();
    }
}
