package org.example.common.dto;

import lombok.Getter;

@Getter
public enum ModerationStatusPost {
    PENDING,
    REJECTION,
    PUBLISHED,
    REMOVED
}
