package com.onelab.courses_service.producer;

import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.NotificationDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendNotification(NotificationDto notificationDto) {
        Message<NotificationDto> message = MessageBuilder
                .withPayload(notificationDto)
                .setHeader(KafkaHeaders.TOPIC, "notification-topic")
                .build();
        kafkaTemplate.send("notification-topic", notificationDto);
    }
}
