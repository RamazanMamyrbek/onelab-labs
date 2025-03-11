package org.onelab.common.dto.response;

import java.util.List;

public record FindAllLessonsWrapper(
        List<LessonResponseDto> list
) {
}
