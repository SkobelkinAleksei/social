package org.example.postmodule.entity;

import lombok.Getter;

@Getter
public enum ModerationStatusPost {
    PENDING,
    REJECTION,
    PUBLISHED,
    REMOVED
}
