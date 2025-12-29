package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.FriendDto;
import org.example.common.dto.RequestData;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendClient {
    private final SecuredHttpCore httpCore;

    public List<FriendDto> getFriends(Long userId) {
        RequestData request = new RequestData(
                "http://localhost:8080/api/v1/social/friends/public/" + userId, // ← правильный URL
                null
        );
        try {
            ResponseEntity<List<FriendDto>> response = httpCore.get(request,
                    (Class<List<FriendDto>>) (Class) List.class);
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            return List.of(); // fallback
        }
    }
}
