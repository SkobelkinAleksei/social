package org.example.friendmodule.entity;

import lombok.Getter;

@Getter
public enum FriendRequestStatus {
    PENDING,   // запрос отправлен
    ACCEPTED,  // друзья
    REJECTED  // отказ
}
