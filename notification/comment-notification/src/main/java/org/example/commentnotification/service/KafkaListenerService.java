package org.example.commentnotification.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.comment.CommentNotificationDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaListenerService {

    @KafkaListener(
            topics = "${kafka.topics.comment-notification.name}",
            containerFactory = "commentListenerFactory"
    )
    public void listen(
            CommentNotificationDto dto
    ) {
        log.info("[KAFKA] Добавлен комментарий: {}", dto);
    }
}
