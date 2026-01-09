package org.example.postmodule.dto;

import lombok.Getter;

@Getter
public enum ModerationStatusPost {
    PENDING,
    REJECTION,
    PUBLISHED,
    REMOVED
}
