package org.example.httpcore.retryPolicy;

import org.springframework.retry.support.RetryTemplate;

public interface RetryPolicyProvider {
    RetryTemplate getRetryTemplate();
}
