package org.example.common.dto.comment;

public record CommentNotificationDto(
        Long authorId,
        Long postId,
        String content
) {
}
