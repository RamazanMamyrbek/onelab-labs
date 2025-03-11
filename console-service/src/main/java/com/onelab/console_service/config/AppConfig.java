package com.onelab.console_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;

@Configuration
@EnableKafka
public class AppConfig {
    @Bean
    public ReplyingKafkaTemplate<String, ?, ?> replyingKafkaTemplate(
            ProducerFactory<String, ?> pf,
            ConcurrentKafkaListenerContainerFactory<String, String> factory,
            ConcurrentMessageListenerContainer<String, ?> repliesContainer) {
        factory.setReplyTemplate(new KafkaTemplate<>(pf));

        ReplyingKafkaTemplate<String, ?, ?> replyingKafkaTemplate = new ReplyingKafkaTemplate<>(pf, repliesContainer);
        replyingKafkaTemplate.setDefaultReplyTimeout(Duration.ofSeconds(10));

        return replyingKafkaTemplate;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, ?> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, ?> containerFactory
    ) {
        ConcurrentMessageListenerContainer<String, ?> repliesContainer = containerFactory.createContainer("console-service.responses-topic");
        repliesContainer.getContainerProperties().setGroupId("education-group");
        return repliesContainer;
    }
}
