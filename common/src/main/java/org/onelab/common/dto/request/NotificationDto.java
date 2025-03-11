package org.onelab.common.dto.request;

public record NotificationDto(
        Long userId,
        String message
) {
}
