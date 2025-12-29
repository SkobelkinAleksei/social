package org.example.common.dto;

public record LoginResponse(
        String token,
        Long userId
) {}