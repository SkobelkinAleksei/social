package org.example.usermodule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.LoginUserDto;
import org.example.usermodule.dto.authDto.RefreshRequestDto;
import org.example.usermodule.dto.authDto.RegistrationUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.security.JwtResponse;
import org.example.usermodule.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/social/v1/public/auth")
@RestController
public class PublicAuthController {
    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<UserDto> createUserAccount(
            @Valid @RequestBody RegistrationUserDto registrationUserDto
    ) {
        return ResponseEntity.ok().body(authService.createUserAccount(registrationUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginUserDto loginUserDto
    ) {
        return ResponseEntity.ok(authService.login(loginUserDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshRequestDto request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }
}
