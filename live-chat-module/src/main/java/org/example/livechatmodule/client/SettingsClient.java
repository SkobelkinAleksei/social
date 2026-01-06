package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.RequestData;
import org.example.common.dto.user.UpdateAccountUserDto;
import org.example.common.dto.user.UpdatePasswordUserDto;
import org.example.common.dto.user.UserDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettingsClient {
    private final SecuredHttpCore httpCore;

    public UserDto updateAccount(UpdateAccountUserDto dto) {
        RequestData request = new RequestData("http://localhost:8080/api/v1/social/users/account/update", dto);
        try {
            ResponseEntity<UserDto> response = httpCore.put(request, UserDto.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Ошибка обновления аккаунта: {}", e.getMessage());
            return null;
        }
    }

    public void updatePassword(UpdatePasswordUserDto dto) {
        if (dto == null) {
            log.error("DTO для смены пароля null");
            throw new IllegalArgumentException("DTO не может быть null");
        }

        log.debug("updatePassword: old={}, new={}",
                dto.getOldPassword() != null ? "OK" : "null",
                dto.getNewPassword() != null ? "OK" : "null");

        RequestData request = new RequestData("http://localhost:8080/api/v1/social/users/account/update/pass", dto);
        try {
            ResponseEntity<Void> response = httpCore.putNoContent(request);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Пароль обновлён (статус: {})", response.getStatusCode());
            } else {
                log.error("❌ Ошибка сервера: {}", response.getStatusCode());
                throw new RuntimeException("Сервер: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("❌ Ошибка смены пароля: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка: " + e.getMessage(), e);
        }
    }
}
