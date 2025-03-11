package com.onelab.notification_service.service;

import com.onelab.notification_service.entity.Notification;
import org.onelab.common.dto.request.NotificationDto;
import org.onelab.common.dto.response.NotificationResponseDto;

import java.util.List;

public interface NotificationService {
    Notification saveNotification(NotificationDto notificationDto);

    List<NotificationDto> getAll();

    List<NotificationResponseDto> getNotificationsByUserId(Long userId);
}
