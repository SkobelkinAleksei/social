package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.friend.FriendDto;
import org.example.common.dto.RequestData;
import org.example.common.dto.friend.FriendRequestDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendClient {
    private final SecuredHttpCore httpCore;
    private final FriendRequestClient friendRequestClient;
    private static final String FRIENDS_PUBLIC = "http://localhost:8085/api/v1/social/friends/public";
    private static final String PRIVATE_URL = "http://localhost:8085/api/v1/social/friends/private";

    public String getFriendshipStatusSync(Long currentUserId, Long targetUserId) {
        try {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                // 1. Друзья
                if (getFriends(currentUserId).stream().anyMatch(f ->
                        (f.getUserId1().equals(currentUserId) && f.getUserId2().equals(targetUserId)) ||
                                (f.getUserId1().equals(targetUserId) && f.getUserId2().equals(currentUserId)))) {
                    return "FRIENDS";
                }

                // 2. ТОЛЬКО PENDING
                List<FriendRequestDto> pendingOnly = friendRequestClient.getByStatus("PENDING", 0, 100);
                if (pendingOnly.stream().anyMatch(r -> r.getAddresseeId().equals(targetUserId))) {
                    return "PENDING_OUT";
                }

                // 3. ТОЛЬКО REJECTED
                List<FriendRequestDto> rejectedOnly = friendRequestClient.getByStatus("REJECTED", 0, 100);
                if (rejectedOnly.stream().anyMatch(r -> r.getAddresseeId().equals(targetUserId))) {
                    return "REJECTED";
                }

                // 4. Входящие PENDING
                List<FriendRequestDto> incoming = friendRequestClient.getIncoming(0, 100);
                if (incoming.stream().anyMatch(r -> r.getRequesterId().equals(targetUserId) && "PENDING".equals(r.getStatus()))) {
                    return "PENDING_IN";
                }

                return "NONE";
            });
            return future.get(4, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Status error: {}", e.getMessage());
            return "NONE";
        }
    }

    public List<FriendDto> getFriends(Long userId) {
        RequestData request = new RequestData(FRIENDS_PUBLIC + "/" + userId, null);
        try {
            ResponseEntity<FriendDto[]> response = httpCore.get(request, FriendDto[].class);
            FriendDto[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();
        } catch (Exception e) {
            log.error("Ошибка получения друзей userId={}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    public CompletableFuture<Void> deleteFriend(Long userId1, Long friendId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String url = PRIVATE_URL + "?userId2=" + friendId;
                RequestData rd = new RequestData(url, null);
                httpCore.delete(rd);
                log.info("Друг {} удалён", friendId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
