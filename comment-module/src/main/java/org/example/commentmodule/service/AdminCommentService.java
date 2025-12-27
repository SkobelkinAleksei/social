package org.example.commentmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.commentmodule.entity.CommentEntity;
import org.example.commentmodule.entity.CommentStatus;
import org.example.commentmodule.repository.CommentRepository;
import org.example.common.dto.RequestData;
import org.example.common.dto.Role;
import org.example.common.dto.UserDto;
import org.example.common.dto.UserFullDto;
import org.example.httpcore.httpCore.IHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class AdminCommentService {
    private final CommentRepository commentRepository;
    private final IHttpCore iHttpCore;

    @Transactional
    public String deleteCommentById(Long commentId, Long adminId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментарий не был найден!"));
        UserFullDto dtoFromApi = getUserDtoFromApi(adminId);

        if (!Role.ADMIN.equals(dtoFromApi.getRole())) {
            throw new IllegalArgumentException("Пользователь не является ADMIN");
        }

        commentEntity.setCommentStatus(CommentStatus.REMOVED);

        return commentEntity.getCommentStatus().toString();
    }

    private UserFullDto getUserDtoFromApi(Long adminId) {
        RequestData requestData = new RequestData(
                "http://localhost:8080/social/v1/admin/users/%s/profile-user"
                        .formatted(adminId),
                null
        );

        ResponseEntity<UserFullDto> userDtoResponseEntity =
                iHttpCore.get(requestData, null, UserFullDto.class);

        if (isNull(userDtoResponseEntity.getBody())) {
            throw new EntityNotFoundException("Пользователь по GET запросу не найден!.");
        }
        return userDtoResponseEntity.getBody();
    }
}
