package org.example.usermodule.controller.restController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.security.SecurityUtil;
import org.example.usermodule.dto.*;
import org.example.usermodule.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/social/users")
@RestController
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<UserFullDto> getMyProfile(
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос на получение профиля пользователя с id: {}", currentUserId);
        return ResponseEntity.ok().body(userService.getMyProfile(currentUserId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/post")
    public ResponseEntity<UserDto> getUserById(
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос на получение пользователя по id: {}", currentUserId);
        return ResponseEntity.ok().body(userService.getUserById(currentUserId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/public/{userId}")
    public ResponseEntity<UserDto> getUserPublic(@PathVariable Long userId) {
        log.info("[INFO] Пришел запрос на получение пользователя по id={} для друзей", userId);
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/search/by-email")
    public ResponseEntity<UserDto> getUserByEmail(
            @RequestParam String email
    ) {
        log.info("[INFO] Пришел запрос на поиск пользователя по email: {}", email);
        return ResponseEntity.ok().body(userService.getUserByEmail(email));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/search")
    public ResponseEntity<List<UserDto>> search(
            @RequestBody(required = false) UserFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("[INFO] Пришел запрос на поиск пользователей по фильтру: {}, страница: {}, размер: {}",
                filter, page, size);
        return ResponseEntity.ok().body(userService.searchUsers(filter, page, size));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/account/update")
    public ResponseEntity<UserDto> updateUserAccount(
            @Valid @RequestBody UpdateAccountUserDto updateAccountUser
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос на обновление аккаунта пользователя с id: {}", currentUserId);
        return ResponseEntity.ok().body(userService.updateUserAccount(currentUserId, updateAccountUser));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/account/update/pass")
    public ResponseEntity<Void> updatePasswordUser(
            @Valid @RequestBody UpdatePasswordUserDto updatePasswordUserDto
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос на обновление пароля пользователя с id: {}", currentUserId);
        userService.updatePassword(currentUserId, updatePasswordUserDto);
        return ResponseEntity.noContent().build();
    }
}
