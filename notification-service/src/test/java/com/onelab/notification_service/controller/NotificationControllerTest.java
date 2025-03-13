package com.onelab.notification_service.controller;

import com.onelab.notification_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.response.NotificationResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @Test
    void shouldGetNotificationsByUserId() {
        Long userId = 1L;
        List<NotificationResponseDto> mockNotifications = List.of(
                new NotificationResponseDto(1L, "Notification 1", LocalDateTime.now()),
                new NotificationResponseDto(2L, "Notification 2", LocalDateTime.now())
        );

        when(notificationService.getNotificationsByUserId(userId)).thenReturn(mockNotifications);

        List<NotificationResponseDto> result = notificationController.getNotificationsByUserId(userId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(NotificationResponseDto::userId)
                .containsExactly(1L, 2L);
        assertThat(result).extracting(NotificationResponseDto::message)
                .containsExactly("Notification 1", "Notification 2");

        verify(notificationService, times(1)).getNotificationsByUserId(userId);
    }
}
