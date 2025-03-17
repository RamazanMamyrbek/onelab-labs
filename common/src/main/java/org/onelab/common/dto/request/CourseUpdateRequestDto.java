package org.onelab.common.dto.request;

public record CourseUpdateRequestDto(Long courseId, String newName, String description, Long price) {}