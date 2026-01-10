package org.example.friendmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.friend.FriendNotificationDto;
import org.example.common.security.SecurityUtil;
import org.example.friendmodule.entity.FriendEntity;
import org.example.friendmodule.repository.FriendRepository;
import org.example.friendmodule.repository.FriendRequestRepository;
import org.example.friendmodule.util.FriendLookupService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrivateFriendService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendNotificationService friendNotificationService;

    @Transactional
    public void deleteFriendById(Long currentUserId, Long userId2) {

        Optional<FriendEntity> friendOpt = friendRepository.findEntityByUserId1AndUserId2(currentUserId, userId2);
        if (friendOpt.isEmpty()) {
            log.warn("Дружба {}<>{} не найдена", currentUserId, userId2);
            return;
        }
        FriendEntity friendEntity = friendOpt.get();

        Optional<Long> optionalLong = friendRequestRepository
                .findRequestIdByAddresseeIdAndRequesterId(currentUserId, userId2);
        optionalLong.ifPresent(requestId -> {
            friendRequestRepository.deleteById(requestId);
            log.info("[INFO] Request {} удалён", requestId);
        });

        FriendNotificationDto friendNotificationDto = new FriendNotificationDto(currentUserId, userId2);
        friendNotificationService.deleteFriendNotification(friendNotificationDto);

        friendRepository.deleteById(friendEntity.getId());
        log.info("[INFO] Дружба была разорвана.");
    }
}
