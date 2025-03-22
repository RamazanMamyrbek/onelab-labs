package org.onelab.common.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewCreateRequestDto(
        Long courseId,
        String text,
        @Min(value = 1, message = "Should be min 1")
        @Max(value = 5, message = "Should be max 5")
        Long rating
) {
}
