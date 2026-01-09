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
import org.example.common.dto.post.PostDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PrivateCommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentLookupService commentLookupService;

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
    public String deleteCommentById(Long commentId, Long currentUserId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий не найден!"));

        boolean isCommentAuthor = commentEntity.getAuthorId().equals(currentUserId);
        boolean isPostOwner = commentEntity.getPostId() != null &&
                commentLookupService.getPostDtoFromApi(commentEntity.getPostId()).getAuthorId().equals(currentUserId);

        if (!isCommentAuthor && !isPostOwner) {
            throw new IllegalArgumentException("Нет прав на удаление!");
        }

        commentEntity.setCommentStatus(CommentStatus.REMOVED);
        return commentEntity.getCommentStatus().toString();
    }
}
