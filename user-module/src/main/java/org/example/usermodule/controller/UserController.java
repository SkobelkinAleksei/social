package org.example.usermodule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.*;
import org.example.usermodule.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/social/v1/users")
@RestController
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/my-profile")
    public ResponseEntity<UserFullDto> getMyProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role
    ) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        return ResponseEntity.ok().body(userService.getMyProfile(userId, role));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @GetMapping("/search/by-email")
    public ResponseEntity<UserDto> getUserByEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok().body(userService.getUserByEmail(email));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> search(
            UserFilterDto filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(userService.searchUsers(filter, page, size));
    }

    @PutMapping("/account/{userId}/update")
    public ResponseEntity<UserDto> updateUserAccount(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAccountUserDto updateAccountUser
    ) throws AccessDeniedException {
        return ResponseEntity.ok().body(userService.updateUserAccount(userId, updateAccountUser));
    }

    @PutMapping("/account/{userId}/update/pass")
    public ResponseEntity<Void> updatePasswordUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePasswordUserDto updatePasswordUserDto
    ) throws AccessDeniedException {
        userService.updatePassword(userId, updatePasswordUserDto);
        return ResponseEntity.noContent().build();
    }
}
