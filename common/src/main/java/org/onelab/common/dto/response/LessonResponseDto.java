package org.onelab.common.dto.response;

public record LessonResponseDto(
        Long id,
        String title,
        Long courseId
) {
}
