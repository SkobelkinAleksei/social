package org.example.kafka.kafkaConfig;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.common.dto.comment.CommentNotificationDto;
import org.example.common.dto.friend.FriendNotificationDto;
import org.example.common.dto.friend.FriendNotificationResponseDto;
import org.example.common.dto.post.LikePostNotificationDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaValueConfig kafkaValueConfig;
    private final ObjectMapper objectMapper;

    // Универсальный producer для всех DTO
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaValueConfig.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Универсальный метод для consumerFactory
    private ConsumerFactory<String, ?> consumerFactory(Class<?> dtoClass, String groupKey) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaValueConfig.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaValueConfig.getGroup(groupKey));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        JsonDeserializer<?> deserializer = new JsonDeserializer<>(dtoClass, objectMapper);
        deserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.application.name", havingValue = "friend-notification")
    public ConcurrentKafkaListenerContainerFactory<String, FriendNotificationDto> requestListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FriendNotificationDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory((
                ConsumerFactory<String, FriendNotificationDto>)
                    consumerFactory(FriendNotificationDto.class, "friend-request")
        );
        factory.setConcurrency(1);
        return factory;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.application.name", havingValue = "friend-notification")
    public ConcurrentKafkaListenerContainerFactory<String, FriendNotificationResponseDto> responseListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FriendNotificationResponseDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(
                (ConsumerFactory<String, FriendNotificationResponseDto>)
                        consumerFactory(FriendNotificationResponseDto.class, "friend-response"));
        factory.setConcurrency(1);
        return factory;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.application.name", havingValue = "friend-notification")
    public ConcurrentKafkaListenerContainerFactory<String, FriendNotificationDto> deleteListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FriendNotificationDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(
                (ConsumerFactory<String, FriendNotificationDto>)
                        consumerFactory(FriendNotificationDto.class, "friend-delete"));
        factory.setConcurrency(1);
        return factory;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.application.name", havingValue = "like-notification")
    public ConcurrentKafkaListenerContainerFactory<String, LikePostNotificationDto> likeListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LikePostNotificationDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory((
                ConsumerFactory<String, LikePostNotificationDto>)
                consumerFactory(LikePostNotificationDto.class, "like")
        );
        factory.setConcurrency(1);
        return factory;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.application.name", havingValue = "comment-notification")
    public ConcurrentKafkaListenerContainerFactory<String, CommentNotificationDto> commentListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CommentNotificationDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory((
                ConsumerFactory<String, CommentNotificationDto>)
                consumerFactory(CommentNotificationDto.class, "comment")
        );
        factory.setConcurrency(1);
        return factory;
    }
}

