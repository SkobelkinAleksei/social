package org.example.usermodule.dto.authDto;

public record LoginResponse(
        String token,
        Long userId
) {}