package org.example.friendmodule.mapper;

import org.example.friendmodule.dto.FriendRequestDto;
import org.example.friendmodule.entity.FriendRequestEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper {
    FriendRequestDto toDto(FriendRequestEntity friendRequestEntity);
}