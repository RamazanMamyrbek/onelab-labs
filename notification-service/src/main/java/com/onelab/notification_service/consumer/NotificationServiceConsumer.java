package com.onelab.notification_service.consumer;

import com.onelab.notification_service.entity.Notification;
import com.onelab.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onelab.common.dto.request.NotificationDto;
import org.onelab.common.dto.response.ErrorResponse;
import org.onelab.common.dto.response.FindAllNotificationsWrapper;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.List;

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


    @KafkaListener(topics = {"notification.request.getNotifications"}, groupId = "education-group")
    @SendTo
    public Object consumeGetNotifications(@Payload Long userId) {
        try {
            List<NotificationResponseDto> notifications = notificationService.getNotificationsByUserId(userId);
            return new FindAllNotificationsWrapper(notifications);
        } catch (Exception e) {
            log.info("Error while getting notifications");
            return new ErrorResponse("Error while getting notifications: %s".formatted(e.getMessage()));
        }
    }
}
