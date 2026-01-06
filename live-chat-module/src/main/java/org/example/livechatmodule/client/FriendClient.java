package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.friend.FriendDto;
import org.example.common.dto.RequestData;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Component
@RequiredArgsConstructor
public class FriendClient {
    private final SecuredHttpCore httpCore;
    private static final String BASE_URL = "http://localhost:8085/api/v1/social/friends/private";

    public List<FriendDto> getFriends(Long userId) {
        RequestData request = new RequestData(
                "http://localhost:8085/api/v1/social/friends/public/" + userId,
                null
        );
        try {
            ResponseEntity<FriendDto[]> response = httpCore.get(request, FriendDto[].class);
            FriendDto[] body = response.getBody();
            List<FriendDto> friends = body != null ? Arrays.asList(body) : List.of();

            log.info("[INFO] FriendClient: userId={}, найдено друзей={}", userId, friends.size());
            friends.forEach(f -> log.info("Friend: id={}, userId1={}, userId2={}",
                    f.getId(), f.getUserId1(), f.getUserId2()));

            return friends;
        } catch (Exception e) {
            log.error("[ERROR] Ошибка получения друзей userId={}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    public CompletableFuture<Void> deleteFriend(Long userId1, Long friendId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String url = BASE_URL + "?userId2=" + friendId;
                RequestData rd = new RequestData(url, null);
                httpCore.delete(rd);
                log.info("[INFO] ✅ Друг с ID={} успешно удалён", friendId);
            } catch (Exception e) {
                log.error("[ERROR] Ошибка удаления друга: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

}
