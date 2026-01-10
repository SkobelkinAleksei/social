package org.example.friendmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.friend.FriendNotificationDto;
import org.example.common.dto.friend.FriendNotificationResponseDto;
import org.example.friendmodule.dto.FriendRequestDto;
import org.example.friendmodule.entity.FriendEntity;
import org.example.friendmodule.entity.FriendRequestEntity;
import org.example.friendmodule.entity.FriendRequestStatus;
import org.example.friendmodule.entity.ResponseFriendRequest;
import org.example.friendmodule.mapper.FriendRequestMapper;
import org.example.friendmodule.repository.FriendRequestRepository;
import org.example.friendmodule.util.FriendRequestSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendService friendService;
    private final FriendRequestMapper friendRequestMapper;
    private final FriendNotificationService friendNotificationService;

    @Transactional
    public FriendRequestDto addFriendRequest(Long requesterId, Long addresseeId) {

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
        } else if (status.equals(ResponseFriendRequest.REJECTED)) {
            requestEntity.setStatus(FriendRequestStatus.REJECTED);
        }


        FriendNotificationResponseDto responseDto = new FriendNotificationResponseDto(
                requestId,
                requestEntity.getRequesterId(),
                status.toString()
        );
        friendNotificationService.responseToRequestNotification(responseDto);

        return requestEntity.getStatus().toString();
    }
}
