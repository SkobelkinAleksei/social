package org.example.livechatmodule.service.kafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.common.NotificationBroadcaster;
import org.example.common.dto.post.LikePostDto;
import org.example.common.dto.post.PostDto;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.LikeClient;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.UserClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.example.common.dto.post.LikePostNotificationDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLikePostListenerService {
    private final NotificationBroadcaster broadcaster;
    private final ObjectMapper objectMapper;
    private final UserClient userClient;
    private final PostClient postClient;

    @KafkaListener(topics = "${kafka.topics.like-notification.name}",
            groupId = "${kafka.groups.like-notification}")
    public void handleLike(ConsumerRecord<String, String> record) {
        try {
            log.info("[LIVECHAT-KAFKA] RAW like: {}", record.value());
            LikePostNotificationDto dto = objectMapper.readValue(record.value(), LikePostNotificationDto.class);
            PostDto post = postClient.getPostById(dto.postId()).join();
            String likerName = getFullName(dto.authorId());  // Кто лайкнул
            String message = "🔔 " + likerName + " лайкнул ваш пост!";

            // Уведомление АВТОРУ поста
            broadcaster.broadcast(new NotificationBroadcaster.NotificationEvent(
                    "post_like", post.getAuthorId(), message));
        } catch (Exception e) {
            log.error("[LIVECHAT-KAFKA-ERROR] Like ошибка: {}", e.getMessage());
        }
    }


    private String getFullName(Long userId) {
        try {
            UserDto user = userClient.getUserById(userId);
            if (user != null) {
                return user.getFirstName() + " " + user.getLastName();
            }
        } catch (Exception e) {
            log.debug("[DEBUG] getFullName fallback: {}", userId);
        }
        return "Пользователь ID " + userId;
    }
}
