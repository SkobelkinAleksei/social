package org.example.livechatmodule.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.friend.FriendDto;
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
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostClient {

    private final SecuredHttpCore http;
    private final FriendClient friendClient;

    private static final String BASE_URL = "http://localhost:8082/api/v1/social/posts";

    public List<PostDto> getUserPosts(Long userId) {
        RequestData rd = new RequestData(BASE_URL + "/user/" + userId, null);
        ResponseEntity<PostDto[]> resp = http.get(rd, PostDto[].class);
        PostDto[] body = resp.getBody();
        return body == null ? List.of() : Arrays.asList(body);
    }

    public CompletableFuture<PostDto> getPostByIdForUser(Long postId, Long userId) {
        log.info("[INFO] PostClient.viewPost: postId={}", postId);
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestData rd = new RequestData(BASE_URL + "/" + postId, null);
                ResponseEntity<PostDto> resp = http.get(rd, PostDto.class);
                return resp.getBody();
            } catch (Exception e) {
                log.error("[ERROR] Ошибка viewPost {}: ", postId, e);
                return null;
            }
        });
    }

    public List<PostDto> getFriendsPosts(Long userId) {
        log.info("Получение постов друзей для userId={}", userId);

        // 1. Получить ID друзей из FriendClient
        List<FriendDto> friends = friendClient.getFriends(userId);
        List<Long> friendIds = friends.stream()
                .map(friend -> {
                    // FriendDto имеет userId1/userId2
                    if (friend.getUserId1().equals(userId)) {
                        return friend.getUserId2();
                    } else {
                        return friend.getUserId1();
                    }
                })
                .filter(id -> !id.equals(userId))  // не самого себя
                .distinct()
                .toList();

        if (friendIds.isEmpty()) {
            log.info("[INFO] У пользователя {} нет друзей", userId);
            return List.of();
        }

        // 2. Для каждого друга получить посты
        List<PostDto> allFriendsPosts = friendIds.stream()
                .flatMap(friendId -> getUserPosts(friendId).stream())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))  // сортировка по дате
                .limit(50)  // лимит, чтоб не тормозило
                .collect(Collectors.toList());

        log.info("[INFO] Найдено {} постов от {} друзей для userId={}",
                allFriendsPosts.size(), friendIds.size(), userId);
        return allFriendsPosts;
    }

    public CompletableFuture<Long> submitPost(NewPostDto dto) {
        log.info("[INFO] PostClient.submitPost: {}", dto.getContent());
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<Long> response = http.post(
                        new RequestData(BASE_URL + "/submit", dto),
                        Long.class
                );
                log.info("[INFO] Сервер ответил: {}", response.getBody());
                return response.getBody();
            } catch (Exception e) {
                log.error("[ERROR] Ошибка POST /submit: ", e);
                throw new RuntimeException("Ошибка сервера: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<PostDto> updatePost(Long postId, UpdatePostDto dto) {
        log.info("[INFO] PostClient.updatePost: postId={}, content={}", postId, dto.getContent());
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestData rd = new RequestData(BASE_URL + "/" + postId + "/update-post", dto);
                ResponseEntity<PostDto> response = http.put(rd, PostDto.class);
                log.info("[INFO] Сервер ответил updatePost: {}", response.getBody());
                return response.getBody();
            } catch (HttpStatusCodeException e) {
                log.error("[ERROR] HTTP ошибка updatePost {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
                throw new RuntimeException("HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
            } catch (Exception e) {
                log.error("[ERROR] Ошибка updatePost: ", e);
                throw new RuntimeException("Ошибка сервера: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<Long> getViewsCount(Long postId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestData rd = new RequestData(BASE_URL + "/" + postId + "/views-count", null);
                ResponseEntity<Long> resp = http.get(rd, Long.class);
                return resp.getBody() != null ? resp.getBody() : 0L;
            } catch (Exception e) {
                log.error("[ERROR] Ошибка views-count: ", e);
                return 0L;
            }
        });
    }

    public CompletableFuture<List<Long>> getPostViews(Long postId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestData rd = new RequestData(BASE_URL + "/" + postId + "/viewers", null);
                ResponseEntity<Long[]> resp = http.get(rd, Long[].class);
                Long[] body = resp.getBody();
                return body != null ? Arrays.asList(body) : List.of();
            } catch (Exception e) {
                log.error("[ERROR] Ошибка viewers: ", e);
                return List.of();
            }
        });
    }
}
