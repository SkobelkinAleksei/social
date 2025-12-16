package org.example.usermodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.dto.UserFullDto;
import org.example.usermodule.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/social/v1/admin/user")
@RestController
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/all-users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok().body(adminService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(adminService.getUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        adminService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/profile-user")
    public ResponseEntity<UserFullDto> getUserProfileById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(adminService.getUserProfileById(userId));
    }
}
