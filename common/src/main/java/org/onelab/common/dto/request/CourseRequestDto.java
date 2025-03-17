package org.onelab.common.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CourseRequestDto(

        @NotBlank(message = "Course name should not be blank")
        String name,

        String description,

        @Min(value = 0, message = "Course price should be a positive number")
        Long price
) {}
