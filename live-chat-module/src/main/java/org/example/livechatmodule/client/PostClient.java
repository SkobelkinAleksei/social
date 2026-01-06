package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.post.NewPostDto;
import org.example.common.dto.post.PostDto;
import org.example.common.dto.RequestData;
import org.example.common.dto.post.UpdatePostDto;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostClient {

    private final SecuredHttpCore http;

    private static final String BASE_URL = "http://localhost:8082/api/v1/social/posts";

    public List<PostDto> getUserPosts(Long userId) {
        RequestData rd = new RequestData(BASE_URL + "/user/" + userId, null);
        ResponseEntity<PostDto[]> resp = http.get(rd, PostDto[].class);
        PostDto[] body = resp.getBody();
        return body == null ? List.of() : Arrays.asList(body);
    }

    public CompletableFuture<Long> submitPost(NewPostDto dto) {  // ← ОСТАВИТЬ ТОЛЬКО ЭТОТ
        log.info("PostClient.submitPost: {}", dto.getContent());
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<Long> response = http.post(
                        new RequestData(BASE_URL + "/submit", dto),
                        Long.class
                );
                log.info("Сервер ответил: {}", response.getBody());
                return response.getBody();
            } catch (Exception e) {
                log.error("Ошибка POST /submit: ", e);
                throw new RuntimeException("Ошибка сервера: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<PostDto> updatePost(Long postId, UpdatePostDto dto) {
        log.info("PostClient.updatePost: postId={}, content={}", postId, dto.getContent());
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestData rd = new RequestData(BASE_URL + "/" + postId + "/update-post", dto);
                ResponseEntity<PostDto> response = http.put(rd, PostDto.class);
                log.info("✅ Сервер ответил updatePost: {}", response.getBody());
                return response.getBody();
            } catch (HttpStatusCodeException e) {
                log.error("❌ HTTP ошибка updatePost {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
                throw new RuntimeException("HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
            } catch (Exception e) {
                log.error("❌ Ошибка updatePost: ", e);
                throw new RuntimeException("Ошибка сервера: " + e.getMessage(), e);
            }
        });
    }
}
