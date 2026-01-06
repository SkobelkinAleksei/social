package org.example.commentmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.commentmodule.dto.CommentDto;
import org.example.commentmodule.dto.NewCommentDto;
import org.example.commentmodule.service.PrivateCommentService;
import org.example.common.security.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/social/comments/private")
@RestController
public class PrivateCommentController {

    private final PrivateCommentService commentService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/update/{commentId}")
    public ResponseEntity<CommentDto> updateCommentById(
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody NewCommentDto newCommentDto
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok().body(
                commentService.updateCommentById(commentId, currentUserId, newCommentDto)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping
    public ResponseEntity<String> deleteCommentById(
            @RequestParam(name = "commentId") Long commentId
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok().body(
                commentService.deleteCommentById(commentId, currentUserId)
        );
    }
}
