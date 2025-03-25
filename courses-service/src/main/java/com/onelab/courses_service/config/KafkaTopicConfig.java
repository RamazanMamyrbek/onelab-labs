package com.onelab.courses_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic reviewCreated() {
        return TopicBuilder
                .name("review.created")
                .build();
    }

    @Bean
    public NewTopic reviewDecision() {
        return TopicBuilder
                .name("review.decision")
                .build();
    }
}
