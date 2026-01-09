package org.example.security.service;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.TokenGenerationRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {
    private final LoginUtil loginUtil;

    @Override
    public String generateAccessToken(TokenGenerationRequest request) {
        return loginUtil.generateAccessToken(request);
    }
}