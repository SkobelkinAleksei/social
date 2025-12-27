package org.example.commentmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.commentmodule.dto.CommentDto;
import org.example.commentmodule.dto.NewCommentDto;
import org.example.commentmodule.service.PublicCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/social/comments/public")
@RestController
public class PublicCommentController {
    private final PublicCommentService commentService;

    @PutMapping("/post/{postId}")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable(name = "postId") Long postId,
            @RequestParam(name = "authorId") Long authorId,
            @RequestBody NewCommentDto newCommentDto
    ) {
        return ResponseEntity.ok().body(commentService.createComment(authorId, postId, newCommentDto));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
        @PathVariable(name = "commentId") Long commentId
    ) {
        return ResponseEntity.ok().body(commentService.getCommentById(commentId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(
            @PathVariable(name = "postId") Long postId
    ) {
        return ResponseEntity.ok().body(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCommentById(
            @RequestParam(name = "commentId") Long commentId,
            @RequestParam(name = "authorId") Long authorId
    ) {
        return ResponseEntity.ok().body(
                commentService.deleteCommentById(commentId, authorId)
        );
    }
}
