package org.onelab.common.dto.response;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        String text,
        Long rating,
        Long courseId,
        Long userId,
        LocalDateTime createdAt
) {
}
