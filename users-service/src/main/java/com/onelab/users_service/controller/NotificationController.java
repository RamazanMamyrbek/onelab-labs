package com.onelab.users_service.controller;

import com.onelab.users_service.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "JWT")
public class NotificationController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> notifications(Principal principal,
                                                                       HttpServletRequest request) {
        List<NotificationResponseDto> responseDtos = userService.getNotifications(principal.getName(),request.getHeader("Authorization"));
        return ResponseEntity.ok(responseDtos);
    }
}
