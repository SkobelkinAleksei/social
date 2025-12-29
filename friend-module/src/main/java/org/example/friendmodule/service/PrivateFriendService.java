package org.example.friendmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.security.SecurityUtil;
import org.example.friendmodule.entity.FriendEntity;
import org.example.friendmodule.repository.FriendRepository;
import org.example.friendmodule.util.FriendLookupService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrivateFriendService {
    private final FriendRepository friendRepository;
    private final FriendLookupService friendLookupService;
    private final FriendRequestService friendRequestService;

    @Transactional
    public void deleteFriendById(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("id могут быть равными");
        }

        Long userId1FromApi = friendLookupService.getUserIdFromApi(userId1);
        Long userId2FromApi = friendLookupService.getUserIdFromApi(userId2);

        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (currentUserId.equals(userId1FromApi) || currentUserId.equals(userId2FromApi)) {
            FriendEntity friendEntity = friendRepository
                    .findEntityByUserId1AndUserId2(userId1FromApi, userId2FromApi);

            friendRepository.deleteById(friendEntity.getId());
            friendRequestService.deleteRequestById(friendEntity.getId());
            log.info("[INFO] Дружба была разорвана.");
        } else {
            throw new AccessDeniedException("Нет доступа для удаления друга, Вы не являетесь другом!");
        }
    }
}
