package org.onelab.common.dto.response;

import java.util.List;

public record FindAllCoursesWrapper(
        List<CourseResponseDto> list
) {
}
