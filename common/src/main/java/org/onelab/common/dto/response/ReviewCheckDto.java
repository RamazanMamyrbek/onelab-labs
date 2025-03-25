package org.onelab.common.dto.response;

public record ReviewCheckDto(
        Long reviewId,
        String text,
        Long userId
) {
}
