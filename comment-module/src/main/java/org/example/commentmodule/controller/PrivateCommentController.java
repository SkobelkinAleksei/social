package org.example.commentmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.commentmodule.dto.CommentDto;
import org.example.commentmodule.dto.NewCommentDto;
import org.example.commentmodule.service.PrivateCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/social/comments/private")
@RestController
public class PrivateCommentController {

    private final PrivateCommentService commentService;

    @PutMapping("/update/{commentId}")
    public ResponseEntity<CommentDto> updateCommentById(
            @PathVariable(name = "commentId") Long commentId,
            @RequestParam(name = "authorId") Long authorId,
            @RequestBody NewCommentDto newCommentDto
    ) {
        return ResponseEntity.ok().body(
                commentService.updateCommentById(commentId, authorId, newCommentDto)
        );
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCommentById(
            @RequestParam(name = "commentId") Long commentId,
            @RequestParam(name = "postAuthorId") Long postAuthorId
    ) {
        return ResponseEntity.ok().body(
                commentService.deleteCommentById(commentId, postAuthorId)
        );
    }
}
