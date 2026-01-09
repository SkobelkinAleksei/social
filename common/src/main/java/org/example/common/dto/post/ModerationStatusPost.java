package org.example.common.dto.post;

import lombok.Getter;

@Getter
public enum ModerationStatusPost {
    PENDING,
    REJECTION,
    PUBLISHED,
    REMOVED
}
