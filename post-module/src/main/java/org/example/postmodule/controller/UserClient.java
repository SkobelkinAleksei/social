package org.example.postmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final WebClient.Builder webClientBuilder;

    public UserDto getUserById(Long userId) {
        return webClientBuilder.build()
                .get()
                .uri("/social/v1/users/{userId}" + userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
}