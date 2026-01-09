package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.comment.CommentDto;
import org.example.common.dto.comment.NewCommentDto;
import org.example.common.dto.RequestData;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentClient {
    private final SecuredHttpCore http;

    private static final String BASE_URL = "http://localhost:8084/api/v1/social/comments/public";

    public List<CommentDto> getCommentsByPostId(Long postId) {
        RequestData rd = new RequestData(BASE_URL + "/post/" + postId, null);
        ResponseEntity<CommentDto[]> resp = http.get(rd, CommentDto[].class);
        CommentDto[] body = resp.getBody();
        return body == null ? List.of() : Arrays.asList(body);
    }

    public void deleteComment(Long commentId) {
        RequestData rd = new RequestData(BASE_URL + "?commentId=" + commentId, null);
        http.delete(rd);
    }

    public CommentDto createComment(Long postId, NewCommentDto newCommentDto) {
        RequestData rd = new RequestData(BASE_URL + "/post/" + postId, newCommentDto);
        ResponseEntity<CommentDto> response = http.put(rd, CommentDto.class);
        return response.getBody();
    }

    public CommentDto updateComment(Long commentId, NewCommentDto newCommentDto) {
        // PRIVATE API для UPDATE
        String privateUrl = "http://localhost:8084/api/v1/social/comments/private/update/" + commentId;
        RequestData rd = new RequestData(privateUrl, newCommentDto);
        ResponseEntity<CommentDto> response = http.put(rd, CommentDto.class);
        return response.getBody();
    }
}
