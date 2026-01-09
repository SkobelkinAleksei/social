package org.example.livechatmodule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

@Configuration
public class RSocketClientConfig {

    @Bean
    public RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder
                .tcp("localhost", 7000);
    }
}