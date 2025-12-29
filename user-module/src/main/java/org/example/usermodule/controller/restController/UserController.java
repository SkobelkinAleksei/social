package org.example.usermodule.controller.restController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usermodule.dto.*;
import org.example.usermodule.dto.authDto.RegistrationUserDto;
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
    @PostMapping("/createUser")
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody RegistrationUserDto registrationUserDto
    ) {
        log.info("[INFO] Пришел запрос на создание пользователя с email: {} и телефоном: {}",
                registrationUserDto.getEmail(), registrationUserDto.getNumberPhone());
        return ResponseEntity.ok().body(userService.createUser(registrationUserDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserFullDto> getMyProfile(
            @PathVariable(name = "userId") Long userId
    ) {
        log.info("[INFO] Пришел запрос на получение профиля пользователя с id: {}", userId);
        return ResponseEntity.ok().body(userService.getMyProfile(userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/post/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.info("[INFO] Пришел запрос на получение пользователя по id (для постов): {}", userId);
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
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> search(
            UserFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("[INFO] Пришел запрос на поиск пользователей по фильтру: {}, страница: {}, размер: {}",
                filter, page, size);
        return ResponseEntity.ok().body(userService.searchUsers(filter, page, size));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/account/{userId}/update")
    public ResponseEntity<UserDto> updateUserAccount(
            @PathVariable(name = "userId") Long userId,
            @Valid @RequestBody UpdateAccountUserDto updateAccountUser
    ) {
        log.info("[INFO] Пришел запрос на обновление аккаунта пользователя с id: {}", userId);
        return ResponseEntity.ok().body(userService.updateUserAccount(userId, updateAccountUser));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/account/{userId}/update/pass")
    public ResponseEntity<Void> updatePasswordUser(
            @PathVariable(name = "userId") Long userId,
            @Valid @RequestBody UpdatePasswordUserDto updatePasswordUserDto
    ) {
        log.info("[INFO] Пришел запрос на обновление пароля пользователя с id: {}", userId);
        userService.updatePassword(userId, updatePasswordUserDto);
        return ResponseEntity.noContent().build();
    }
}
