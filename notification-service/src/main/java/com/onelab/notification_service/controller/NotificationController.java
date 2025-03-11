package com.onelab.notification_service.controller;

import com.onelab.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.NotificationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAll() {
        return ResponseEntity.ok().body(notificationService.getAll());
    }
}
