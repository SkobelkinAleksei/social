package org.example.common.dto.friend;

public record FriendNotificationResponseDto(
        Long requestId,
        Long userId,
        String status
) {
}