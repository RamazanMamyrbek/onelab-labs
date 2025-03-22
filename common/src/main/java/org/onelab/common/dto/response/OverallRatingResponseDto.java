package org.onelab.common.dto.response;

public record OverallRatingResponseDto(
        Long courseId,
        Double overallRating
) {
}
