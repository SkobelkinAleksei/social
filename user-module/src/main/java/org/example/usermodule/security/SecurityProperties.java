package org.example.usermodule.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private String accessSecret;
    private String refreshSecret;
}
