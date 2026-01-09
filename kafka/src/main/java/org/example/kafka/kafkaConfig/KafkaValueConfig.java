package org.example.kafka.kafkaConfig;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
@Slf4j
@Configuration
@EnableKafka
@ConfigurationProperties(prefix = "kafka")
public class KafkaValueConfig {
    private Map<String, TopicProperties> topics;
    private Map<String, String> groups;
    private Environment environment;
    private String bootstrapServers;

    @Bean
    public List<NewTopic> createTopics() {
        if (isNull(topics)) {
            log.info("topics IsNULL");
        }
         return topics.values().stream()
                .map(topic -> new NewTopic(
                        topic.getName(),
                        topic.getPartitions(),
                        topic.getReplication())
                )
                .toList();
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @PostConstruct
    public void init() {
        log.info("Topics: {}, Groups: {}", topics, groups);
    }

    @ToString
    @Getter
    @Setter
    public static class TopicProperties {
        private String name;
        private int partitions;
        private short replication;
    }
}
