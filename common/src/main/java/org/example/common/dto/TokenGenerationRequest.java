package org.example.common.dto;

public record TokenGenerationRequest(
        Long userId,
        String email,
        String role
) {}