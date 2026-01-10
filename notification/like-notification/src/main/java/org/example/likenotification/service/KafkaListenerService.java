package org.example.likenotification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.example.common.dto.post.LikePostNotificationDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaListenerService {

    @KafkaListener(
            topics = "${kafka.topics.like-notification.name}",
            containerFactory = "likeListenerFactory"
    )
    public void listen(
            LikePostNotificationDto dto
    ) {
        log.info("[KAFKA] Поставлен лайк: {}", dto);
    }
}
