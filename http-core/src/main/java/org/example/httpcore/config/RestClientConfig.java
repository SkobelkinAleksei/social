package org.example.httpcore.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "values")
public class RestClientConfig {

    @Getter
    @Setter
    private String baseUrl;

    @PostConstruct
    public void init() {
        log.info("[INFO] baseUrl: {}", baseUrl);
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
