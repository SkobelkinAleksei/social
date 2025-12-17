package org.example.usermodule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.*;
import org.example.usermodule.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/social/v1/users")
@RestController
public class UserController {
    private final UserService userService;

//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/my-profile")
    public ResponseEntity<UserFullDto> getMyProfile(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok().body(userService.getMyProfile(userId));
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
            @RequestHeader("X-User-Id") Long authId,
            @Valid @RequestBody UpdateAccountUserDto updateAccountUser
    ) throws AccessDeniedException {
        return ResponseEntity.ok().body(userService.updateUserAccount(userId, authId, updateAccountUser));
    }

    @PutMapping("/account/{userId}/update/pass")
    public ResponseEntity<Void> updatePasswordUser(
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long authId,
            @Valid @RequestBody UpdatePasswordUserDto updatePasswordUserDto
    ) throws AccessDeniedException {
        userService.updatePassword(userId, authId, updatePasswordUserDto);
        return ResponseEntity.noContent().build();
    }
}
