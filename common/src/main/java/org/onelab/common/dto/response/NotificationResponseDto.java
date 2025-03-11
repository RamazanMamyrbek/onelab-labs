package org.onelab.common.dto.response;

import java.time.LocalDateTime;

public record NotificationResponseDto(
        Long userId,
        String message,
        LocalDateTime dateTime
) {
}
