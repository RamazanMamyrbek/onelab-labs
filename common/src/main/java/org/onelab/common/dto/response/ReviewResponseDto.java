package org.onelab.common.dto.response;

import org.onelab.common.enums.ReviewStatus;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        String text,
        Long rating,
        Long courseId,
        Long userId,
        LocalDateTime createdAt,

        ReviewStatus reviewStatus
) {
}
