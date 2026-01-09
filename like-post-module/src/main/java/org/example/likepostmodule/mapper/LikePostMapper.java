package org.example.likepostmodule.mapper;

import org.example.likepostmodule.dto.LikePostDto;
import org.example.likepostmodule.entity.LikePostEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikePostMapper {
    LikePostDto toDto(LikePostEntity likePostEntity);
}
