package org.example.usermodule.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.security.utils.SecurityUtils;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.dto.UserFullDto;
import org.example.usermodule.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/social/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RestController
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/all-users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("[INFO] Пришел запрос от ADMIN на получение списка всех пользователей");
        return ResponseEntity.ok().body(adminService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.info("[INFO] Пришел запрос от ADMIN на получение пользователя по id: {}", userId);
        return ResponseEntity.ok().body(adminService.getUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        log.info("[INFO] Пришел запрос от ADMIN на удаление пользователя по id: {}", userId);
        adminService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/profile-user")
    public ResponseEntity<UserFullDto> getUserProfileById(@PathVariable Long userId) {
        log.info("[INFO] Пришел запрос от ADMIN на получение профиля пользователя по id: {}", userId);
        return ResponseEntity.ok().body(adminService.getUserProfileById(userId));
    }
}
