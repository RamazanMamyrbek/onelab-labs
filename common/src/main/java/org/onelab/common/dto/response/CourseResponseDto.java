package org.onelab.common.dto.response;

import java.time.LocalDateTime;

public record CourseResponseDto(
        Long id,
        String name,

        String description,

        Long price,

        LocalDateTime createdAt,
        Long teacherId
) {
}
