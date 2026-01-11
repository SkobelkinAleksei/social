package org.example.common.dto.friend;

public record FriendNotificationDto(
        Long userId,
        Long friendId
) {
}
