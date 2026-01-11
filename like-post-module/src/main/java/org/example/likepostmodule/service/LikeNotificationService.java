package org.example.likepostmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.post.LikePostNotificationDto;
import org.example.kafka.kafkaConfig.KafkaValueConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeNotificationService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaValueConfig kafkaValueConfig;

    public void sendLikeNotification(LikePostNotificationDto dto) {
        try {
            String topic = kafkaValueConfig.getTopics().get("like-notification").getName();
            log.info("[KAFKA-INFO] Получили запрос на отправку уведомления о лайке: {}", dto);
            kafkaTemplate.send(topic, dto);
            log.info("[KAFKA-INFO] Отправлено уведомление о новом лайке: {}", dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}