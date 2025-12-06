package org.example.usermodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/social/v1/users")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.getUserByEmail(email));
    }
}
