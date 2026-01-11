package org.example.commentmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.commentmodule.dto.CommentDto;
import org.example.commentmodule.dto.NewCommentDto;
import org.example.commentmodule.service.PublicCommentService;
import org.example.common.security.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/social/comments/public")
@RestController
public class PublicCommentController {
    private final PublicCommentService commentService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/post/{postId}")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable(name = "postId") Long postId,
            @RequestBody NewCommentDto newCommentDto
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok().body(
                commentService.createComment(currentUserId, postId, newCommentDto)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
        @PathVariable(name = "commentId") Long commentId
    ) {
        return ResponseEntity.ok().body(commentService.getCommentById(commentId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(
            @PathVariable(name = "postId") Long postId
    ) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(postId));
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
