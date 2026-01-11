package org.example.livechatmodule.service.kafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.common.NotificationBroadcaster;
import org.example.common.dto.comment.CommentNotificationDto;
import org.example.common.dto.post.PostDto;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.PostClient;
import org.example.livechatmodule.client.UserClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaCommentListenerService {
    private final NotificationBroadcaster broadcaster;
    private final ObjectMapper objectMapper;
    private final UserClient userClient;
    private final PostClient postClient;

    @KafkaListener(topics = "${kafka.topics.comment-notification.name}",
            groupId = "${kafka.groups.comment-notification}")
    public void handleComment(ConsumerRecord<String, String> record) {
        try {
            log.info("[LIVECHAT-KAFKA] RAW comment: {}", record.value());
            CommentNotificationDto dto = objectMapper.readValue(record.value(), CommentNotificationDto.class);

            String commenterName = getFullName(dto.authorId());
            PostDto post = postClient.getPostById(dto.postId()).join();

            if (post == null) {
                log.warn("[LIVECHAT-KAFKA] Пост postId={} не найден", dto.postId());
                return;
            }

            String message = "🔔 " + commenterName + " прокомментировал ваш пост: \"" +
                    dto.content().substring(0, Math.min(50, dto.content().length())) + "\"";

            broadcaster.broadcast(new NotificationBroadcaster.NotificationEvent(
                    "post_comment", post.getAuthorId(), message));
        } catch (Exception e) {
            log.error("[LIVECHAT-KAFKA-ERROR] Comment: {}", e.getMessage(), e);
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
