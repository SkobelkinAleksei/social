package org.example.common.dto.friend;

public record FriendNotificationRequestDto(
        Long requesterId,
        Long addresseeId
) {
}
