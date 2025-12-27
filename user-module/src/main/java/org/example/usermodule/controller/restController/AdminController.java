package org.example.usermodule.controller.restController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.dto.UserFullDto;
import org.example.usermodule.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/social/v1/admin/users")
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
