package org.example.commentmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.commentmodule.dto.CommentDto;
import org.example.commentmodule.dto.NewCommentDto;
import org.example.commentmodule.entity.CommentEntity;
import org.example.commentmodule.entity.CommentStatus;
import org.example.commentmodule.mapper.CommentMapper;
import org.example.commentmodule.repository.CommentRepository;
import org.example.commentmodule.util.CommentLookupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PublicCommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentLookupService commentLookupService;

    @Transactional
    public CommentDto createComment(Long authorId, Long postId, NewCommentDto newCommentDto) {
        Long userFromApi = commentLookupService.getUserIdFromApi(authorId);
        Long postFromApi = commentLookupService.getPostIdFromApi(postId);

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
        Long postFromApi = commentLookupService.getPostIdFromApi(postId);
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
        Long userFromApi = commentLookupService.getUserIdFromApi(authorId);

        if (!commentEntity.getAuthorId().equals(userFromApi)) {
            throw new IllegalArgumentException("Вы не можете удалить чужой комментарий!");
        }

        commentEntity.setCommentStatus(CommentStatus.REMOVED);

        return commentEntity.getCommentStatus().toString();
    }
}
