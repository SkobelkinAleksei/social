package org.example.friendmodule.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.RequestData;
import org.example.common.dto.UserDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendLookupService {

    private final SecuredHttpCore iHttpCore;

    public Long getUserIdFromApi(Long authorId) {
        log.info("[INFO] Запрос данных пользователя по id: {} во внешний сервис", authorId);
        RequestData requestData = new RequestData(
                "http://localhost:8080/api/v1/social/users/post/%s"
                        .formatted(authorId),
                null
        );

        ResponseEntity<UserDto> userDtoResponseEntity =
                iHttpCore.get(requestData, UserDto.class);

        if (isNull(userDtoResponseEntity.getBody())) {
            log.warn("[INFO] Пользователь с id: {} не найден во внешнем сервисе", authorId);
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }

        Long userId = userDtoResponseEntity.getBody().getUserId();
        log.info("[INFO] Пользователь из внешнего сервиса найден, id: {}", userId);

        return userId;
    }
}
