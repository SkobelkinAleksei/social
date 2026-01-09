package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.RequestData;
import org.example.common.dto.user.UserDto;
import org.example.common.dto.user.UserFilterDto;
import org.example.common.dto.user.UserFullDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClient {
    private final SecuredHttpCore httpCore;

    public UserDto getUserById(Long userId) {
        RequestData request = new RequestData(
                "http://localhost:8080/api/v1/social/users/public/" + userId,
                null
        );
        try {
            ResponseEntity<UserDto> response = httpCore.get(request, UserDto.class);
            UserDto user = response.getBody();
            log.info("[INFO] UserClient: userId={} name={}", userId,
                    user != null ? user.getFirstName() : "null");
            return user;
        } catch (Exception e) {
            log.warn("[WARN] Не удалось загрузить пользователя id={}: {}", userId, e.getMessage());
            return null;
        }
    }

    public UserFullDto getMyProfile() {
        RequestData rd = new RequestData("http://localhost:8080/api/v1/social/users/me", null);
        try {
            ResponseEntity<UserFullDto> resp = httpCore.get(rd, UserFullDto.class);
            UserFullDto user = resp.getBody();

            log.info("[INFO] UserFullDto: id={}, name={}, email={}",
                    user != null ? user.getId() : "NULL",
                    user != null ? user.getFirstName() : "NULL",
                    user != null ? user.getEmail() : "NULL");

            return user;
        } catch (Exception e) {
            log.error("[ERROR] UserService ошибка: {}", e.getMessage());
            return null;
        }
    }

    public List<UserDto> searchUsers(UserFilterDto filter, int page, int size) {
        String url = String.format(
                "http://localhost:8080/api/v1/social/users/search?page=%d&size=%d",
                page, size
        );
        RequestData rd = new RequestData(url, filter);

        try {
            ResponseEntity<UserDto[]> resp = httpCore.post(rd, UserDto[].class);
            UserDto[] array = resp.getBody();
            return array != null ? List.of(array) : List.of();
        } catch (Exception e) {
            log.error("[ERROR] Search API error: {}", e.getMessage());
            return List.of();
        }
    }

    public Long getCurrentUserId() {
        try {
            UserFullDto profile = getMyProfile();
            return profile != null ? profile.getId() : 1L;
        } catch (Exception e) {
            log.warn("[WARN] Не удалось получить currentUserId: {}", e.getMessage());
            return 1L;
        }
    }
}
