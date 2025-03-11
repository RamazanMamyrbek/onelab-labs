package com.onelab.notification_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder
                .name("notification-topic")
                .build();
    }

    @Bean
    public NewTopic getNotificationsTopic() {
        return TopicBuilder
                .name("notification.request.getNotifications")
                .build();
    }
    @Bean
    public NewTopic getNotificationsResponseTopic() {
        return TopicBuilder
                .name("notification-service.responses-topic")
                .build();
    }
}
