package org.example.common.dto.post;

public record LikePostNotificationDto(
    Long postId,
    Long authorId
){}