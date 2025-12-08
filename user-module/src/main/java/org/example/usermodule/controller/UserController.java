package org.example.usermodule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UpdateAccountUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RequestMapping("/social/v1/users")
@RestController
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getUserByEmail(
            @RequestParam String email
    ) {
        return ResponseEntity.ok().body(userService.getUserByEmail(email));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/account/{userId}/update")
    public ResponseEntity<UserDto> updateUserAccount(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAccountUserDto updateAccountUser
    ) throws AccessDeniedException {
        return ResponseEntity.ok().body(userService.updateUserAccount(userId, updateAccountUser));
    }
}
