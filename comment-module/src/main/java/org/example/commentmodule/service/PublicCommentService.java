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

import java.util.List;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class PublicCommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final IHttpCore iHttpCore;

    @Transactional
    public CommentDto createComment(Long authorId, Long postId, NewCommentDto newCommentDto) {
        Long userFromApi = getUserIdFromApi(authorId);
        Long postFromApi = getPostIdFromApi(postId);

        CommentEntity commentEntity = commentMapper.toEntity(newCommentDto);
        commentEntity.setAuthorId(userFromApi);
        commentEntity.setPostId(postFromApi);
        commentEntity.setCommentStatus(CommentStatus.PUBLISHED);

        return commentMapper.toDto(
                commentRepository.save(commentEntity)
        );
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий не был найден!")
        );

        if (commentEntity.getCommentStatus().equals(CommentStatus.REMOVED)) {
            throw new IllegalArgumentException("Комментарий не доступен");
        }

        return commentMapper.toDto(commentEntity);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        Long postFromApi = getPostIdFromApi(postId);
        List<CommentEntity> allByPostId = commentRepository.findAllByPostIdAndStatusPublished(postFromApi);

        return allByPostId.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public String deleteCommentById(Long commentId, Long authorId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий не был найден!")
        );
        Long userFromApi = getUserIdFromApi(authorId);

        if (!commentEntity.getAuthorId().equals(userFromApi)) {
            throw new IllegalArgumentException("Вы не можете удалить чужой комментарий!");
        }

        commentEntity.setCommentStatus(CommentStatus.REMOVED);

        return commentEntity.getCommentStatus().toString();
    }

    private Long getUserIdFromApi(Long authorId) {
        RequestData requestData = new RequestData(
                "http://localhost:8080/api/v1/social/users/post/%s"
                        .formatted(authorId),
                null
        );

        ResponseEntity<UserDto> userDtoResponseEntity =
                iHttpCore.get(requestData, null, UserDto.class);

        if (isNull(userDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }
        return userDtoResponseEntity.getBody().getUserId();
    }

    private Long getPostIdFromApi(Long postId) {
        RequestData requestData = new RequestData(
                "http://localhost:8082/api/v1/social/posts/id/%s".formatted(postId),
                null
        );

        ResponseEntity<PostDto> postDtoResponseEntity = iHttpCore.get(requestData, null, PostDto.class);

        if (isNull(postDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пост по GET запросу не найден!.");
        }

        PostDto body = postDtoResponseEntity.getBody();
        Long postIdFromApi = body.getPostId();

        if (postIdFromApi == null) {
            throw new IllegalStateException("postIdFromApi == null");
        }

        return postIdFromApi;
    }
}
