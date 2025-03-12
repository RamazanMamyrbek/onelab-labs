package com.onelab.notification_service.consumer;

import com.onelab.notification_service.entity.Notification;
import com.onelab.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.dto.request.NotificationDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = {"notification-topic"}, groupId = "education-group")
    public void consumeNotification(NotificationDto notificationDto) {
        Notification notification = notificationService.saveNotification(notificationDto);
        log.info("Saved notification: {}", notification.toString());
    }

}
