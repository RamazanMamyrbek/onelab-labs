package org.onelab.common.dto.response;

public record CourseResponseDto(
        Long id,
        String name,
        Long teacherId
) {
}
