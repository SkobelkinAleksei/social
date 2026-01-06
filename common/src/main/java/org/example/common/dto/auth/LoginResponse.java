package org.example.common.dto.auth;

public record LoginResponse(
        String token,
        Long userId
) {}