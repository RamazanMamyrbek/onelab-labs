package org.onelab.common.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CourseRequestDto(

        @NotBlank(message = "Course name should not be blank")
        String name
) {}
