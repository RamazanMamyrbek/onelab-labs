package com.onelab.notification_service.service;

import com.onelab.notification_service.entity.Notification;
import com.onelab.notification_service.repository.NotificationRepository;
import com.onelab.notification_service.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.NotificationDto;
import org.onelab.common.dto.response.NotificationResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        notificationDto = new NotificationDto(1L, "Test message");
        notification = Notification.builder()
                .id(1L)
                .userId(1L)
                .message("Test message")
                .dateTime(LocalDateTime.of(2024, 3, 11, 12, 0))
                .build();
    }

    @Test
    void saveNotification_ShouldSaveAndReturnNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification savedNotification = notificationService.saveNotification(notificationDto);

        assertThat(savedNotification).isNotNull();
        assertThat(savedNotification.getUserId()).isEqualTo(notificationDto.userId());
        assertThat(savedNotification.getMessage()).isEqualTo(notificationDto.message());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getAll_ShouldReturnAllNotifications() {
        when(notificationRepository.findAll()).thenReturn(List.of(notification));

        List<NotificationDto> result = notificationService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(notification.getUserId());
        assertThat(result.get(0).message()).isEqualTo(notification.getMessage());
        verify(notificationRepository).findAll();
    }

    @Test
    void getNotificationsByUserId_ShouldReturnNotificationsForUser() {
        when(notificationRepository.findAllByUserId(1L)).thenReturn(List.of(notification));

        List<NotificationResponseDto> result = notificationService.getNotificationsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(notification.getUserId());
        assertThat(result.get(0).message()).isEqualTo(notification.getMessage());
        assertThat(result.get(0).dateTime()).isEqualTo(notification.getDateTime());
        verify(notificationRepository).findAllByUserId(1L);
    }
}

