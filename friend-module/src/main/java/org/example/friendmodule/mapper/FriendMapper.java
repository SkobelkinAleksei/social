package org.example.friendmodule.mapper;

import org.example.friendmodule.dto.FriendDto;
import org.example.friendmodule.entity.FriendEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    @Mapping(target = "userId1", source = "userId1")
    @Mapping(target = "userId2", source = "userId2")
    FriendDto toDto(FriendEntity friendEntity);
}