package org.example.apigateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {

        return WebClient.builder()
                .filter((request, next) -> {

                    var context = SecurityContextHolder.getContext();

                    if (context != null) {
                        Authentication auth = context.getAuthentication();

                        if (auth != null && auth.getCredentials() != null) {

                            String token = auth.getCredentials().toString();

                            ClientRequest newRequest = ClientRequest.from(request)
                                    .header("Authorization", "Bearer " + token)
                                    .build();

                            return next.exchange(newRequest);
                        }
                    }

                    return next.exchange(request);
                });
    }
}