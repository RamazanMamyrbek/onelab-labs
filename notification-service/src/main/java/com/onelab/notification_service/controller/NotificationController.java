package com.onelab.notification_service.controller;

import com.onelab.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<NotificationResponseDto> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationResponseDto> responseDtoList = notificationService.getNotificationsByUserId(userId);
        return responseDtoList;
    }
}
