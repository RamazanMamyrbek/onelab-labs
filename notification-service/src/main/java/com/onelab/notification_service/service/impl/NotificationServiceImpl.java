package com.onelab.notification_service.service.impl;

import com.onelab.notification_service.entity.Notification;
import com.onelab.notification_service.repository.NotificationRepository;
import com.onelab.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.NotificationDto;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;


    @Override
    @Transactional
    public Notification saveNotification(NotificationDto notificationDto) {
        Notification notification = Notification
                .builder()
                .userId(notificationDto.userId())
                .message(notificationDto.message())
                .dateTime(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDto> getAll() {
        return notificationRepository.findAll()
                .stream()
                .map(notification -> new NotificationDto(notification.getUserId(), notification.getMessage()))
                .toList();
    }

    @Override
    public List<NotificationResponseDto> getNotificationsByUserId(Long userId) {
        return notificationRepository.findAllByUserId(userId)
                .stream()
                .map(notification -> new NotificationResponseDto(notification.getUserId(), notification.getMessage(), notification.getDateTime()))
                .toList();
    }
}
