package org.example.common.dto.friend;

import lombok.Getter;

@Getter
public enum FriendRequestStatus {
    PENDING,   // запрос отправлен
    ACCEPTED,  // друзья
    REJECTED  // отказ
}
