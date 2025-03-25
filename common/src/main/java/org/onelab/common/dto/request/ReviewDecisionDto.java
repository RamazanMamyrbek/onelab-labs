package org.onelab.common.dto.request;

public record ReviewDecisionDto(
        Long reviewId,
        boolean isApproved
) {
}
