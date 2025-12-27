package org.example.commentmodule.mapper;

import org.example.commentmodule.dto.CommentDto;
import org.example.commentmodule.dto.NewCommentDto;
import org.example.commentmodule.entity.CommentEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentEntity toEntity(CommentDto commentDto);
    CommentEntity toEntity(NewCommentDto commentDto);
    CommentDto toDto(CommentEntity commentEntity);
}