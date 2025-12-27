package org.example.usermodule.controller.restController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.authDto.LoginUserDto;
import org.example.usermodule.dto.authDto.RegistrationUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RequestMapping("/social/public/v1/auth")
@RestController
public class PublicAuthController {
    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<UserDto> signUp(
            @Valid @RequestBody RegistrationUserDto registrationUserDto
    ) {
        return ResponseEntity.ok().body(authService.signUp(registrationUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody LoginUserDto loginUserDto
    ) throws AccessDeniedException {
        return ResponseEntity.ok(authService.login(loginUserDto));
    }
}
