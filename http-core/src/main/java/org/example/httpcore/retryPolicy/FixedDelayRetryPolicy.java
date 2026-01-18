package org.example.httpcore.retryPolicy;

import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FixedDelayRetryPolicy implements RetryPolicyProvider {

    @Override
    public RetryTemplate getRetryTemplate() {
        return RetryTemplate.builder()
                .fixedBackoff(1000)
                .maxAttempts(3)
                .retryOn(List.of(IllegalArgumentException.class))
                .build();
    }
}
