package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.post.LikePostDto;
import org.example.common.dto.RequestData;
import org.example.httpcore.httpCore.SecuredHttpCore;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeClient {
    private final SecuredHttpCore http;

    private static final String BASE_URL = "http://localhost:8083/api/v1/social/likes";

    public CompletableFuture<String> toggleLike(Long postId) {
        log.info("LikeClient.toggleLike: postId={}", postId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestData rd = new RequestData(BASE_URL + "/" + postId, null);
                ResponseEntity<String> response = http.post(rd, String.class);
                log.info("✅ toggleLike postId={} вернул статус: {}", postId, response.getBody());
                return response.getBody();
            } catch (HttpStatusCodeException e) {
                log.error("❌ HTTP ошибка toggleLike postId={} {}: {}", postId, e.getStatusCode(), e.getResponseBodyAsString());
                throw new RuntimeException("HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
            } catch (Exception e) {
                log.error("❌ Ошибка toggleLike postId={}: ", postId, e);
                throw new RuntimeException("Ошибка сервера: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<List<LikePostDto>> getLikes(Long postId) {
        log.info("LikeClient.getLikes: postId={}", postId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestData rd = new RequestData(BASE_URL + "/" + postId + "/list-like", null);
                ResponseEntity<LikePostDto[]> response = http.get(rd, LikePostDto[].class);
                List<LikePostDto> likes = response.getBody() != null ? Arrays.asList(response.getBody()) : List.of();
                log.info("✅ getLikes postId={} вернул {} лайков", postId, likes.size());
                return likes;
            } catch (HttpStatusCodeException e) {
                log.error("❌ HTTP ошибка getLikes postId={} {}: {}", postId, e.getStatusCode(), e.getResponseBodyAsString());
                return List.of();  // Возвращаем пустой список при ошибке
            } catch (Exception e) {
                log.error("❌ Ошибка getLikes postId={}: ", postId, e);
                return List.of();
            }
        });
    }

    public CompletableFuture<Long> getLikesCount(Long postId) {
        log.info("LikeClient.getLikesCount: postId={}", postId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestData rd = new RequestData(BASE_URL + "/" + postId + "/likes-count", null);
                ResponseEntity<Long> response = http.get(rd, Long.class);
                Long count = response.getBody();
                log.info("✅ getLikesCount postId={} = {}", postId, count);
                return count != null ? count : 0L;
            } catch (Exception e) {
                log.error("❌ Ошибка getLikesCount postId={}: ", postId, e);
                return 0L;
            }
        });
    }

}

