package org.example.commentmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.commentmodule.entity.CommentEntity;
import org.example.commentmodule.entity.CommentStatus;
import org.example.commentmodule.repository.CommentRepository;
import org.example.commentmodule.util.CommentLookupService;
import org.example.common.dto.Role;
import org.example.common.dto.UserFullDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminCommentService {
    private final CommentRepository commentRepository;
    private final CommentLookupService commentLookupService;

    @Transactional
    public String deleteCommentById(Long commentId, Long adminId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий не был найден!"));
        UserFullDto dtoFromApi = commentLookupService.getUserDtoFromApi(adminId);

        if (!Role.ADMIN.equals(dtoFromApi.getRole())) {
            throw new IllegalArgumentException("Пользователь не является ADMIN");
        }

        commentEntity.setCommentStatus(CommentStatus.REMOVED);

        return commentEntity.getCommentStatus().toString();
    }
}
