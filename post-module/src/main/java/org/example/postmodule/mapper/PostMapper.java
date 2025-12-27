package org.example.postmodule.mapper;

import org.example.postmodule.dto.NewPostDto;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.dto.PostFullDto;
import org.example.postmodule.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostEntity toEntity(PostDto postDto);

    PostEntity toEntity(NewPostDto newPostDto);

    @Mapping(target = "postId", source = "id")
    PostDto toDto(PostEntity postEntity);

    PostFullDto toFullDto(PostEntity postEntity);

//    @AfterMapping
//    default void linkLikeSet(@MappingTarget PostEntity postEntity) {
//        postEntity.getLikeSet().forEach(likeSet -> likeSet.setPost(postEntity));
//    }
//
//    @AfterMapping
//    default void linkCommentTable(@MappingTarget PostEntity postEntity) {
//        postEntity.getCommentTable().forEach(commentTable -> commentTable.setPost(postEntity));
//    }

}