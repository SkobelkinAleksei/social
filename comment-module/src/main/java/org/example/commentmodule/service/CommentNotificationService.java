package org.example.commentmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.comment.CommentNotificationDto;
import org.example.kafka.kafkaConfig.KafkaValueConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentNotificationService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaValueConfig kafkaValueConfig;

    public void sendCommentNotification(CommentNotificationDto dto) {
        try {
            String topic = kafkaValueConfig.getTopics().get("comment-notification").getName();
            log.info("[KAFKA-INFO] Получили запрос на отправку уведомления о новом комментарии: {}", dto);
            kafkaTemplate.send(topic, dto);
            log.info("[KAFKA-INFO] Отправлено уведомление о новом комментарии: {}", dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
