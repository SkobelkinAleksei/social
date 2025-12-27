package org.example.commentmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.commentmodule.dto.CommentDto;
import org.example.commentmodule.dto.NewCommentDto;
import org.example.commentmodule.entity.CommentEntity;
import org.example.commentmodule.entity.CommentStatus;
import org.example.commentmodule.mapper.CommentMapper;
import org.example.commentmodule.repository.CommentRepository;
import org.example.common.dto.PostDto;
import org.example.common.dto.RequestData;
import org.example.common.dto.UserDto;
import org.example.httpcore.httpCore.IHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class PrivateCommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final IHttpCore iHttpCore;

    @Transactional
    public CommentDto updateCommentById(
            Long commentId,
            Long authorId,
            NewCommentDto newCommentDto
    ) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий не был найден!"));

        if (!commentEntity.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("Нет доступа для изменения комментария!");
        }

        commentEntity.setContent(newCommentDto.getContent());
        return commentMapper.toDto(commentEntity);
    }

    @Transactional
    public String deleteCommentById(Long commentId, Long postAuthorId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий не был найден!"));
        PostDto postDtoFromApi = getPostDtoFromApi(commentEntity.getPostId(), postAuthorId);

        if (!postDtoFromApi.getAuthorId().equals(postAuthorId)) {
            throw new IllegalArgumentException("Нет доступа для удаления комментария!");
        }
        commentEntity.setCommentStatus(CommentStatus.REMOVED);

        return commentEntity.getCommentStatus().toString();
    }

    private PostDto getPostDtoFromApi(Long postId, Long authorId) {
        RequestData requestData = new RequestData(
                "http://localhost:8082/api/v1/social/posts/%s/%s".formatted(authorId, postId),
                null
        );

        ResponseEntity<PostDto> postDtoResponseEntity = iHttpCore.get(requestData, null, PostDto.class);

        if (isNull(postDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пост по GET запросу не найден!.");
        }

        return postDtoResponseEntity.getBody();
    }
}
