package org.example.commentmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.commentmodule.service.AdminCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/social/comments/admin")
@RestController
public class AdminCommentController {

    private final AdminCommentService commentService;

    @DeleteMapping
    public ResponseEntity<String> deleteCommentById(
            @RequestParam(name = "commentId") Long commentId,
            @RequestParam(name = "adminId") Long adminId
    ) {
        return ResponseEntity.ok().body(
                commentService.deleteCommentById(commentId, adminId)
        );
    }
}
