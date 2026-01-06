package org.example.security.service;

import org.example.common.dto.TokenGenerationRequest;

public interface JwtTokenProvider {
    String generateAccessToken(TokenGenerationRequest user);
}
