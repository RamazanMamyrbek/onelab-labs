package org.onelab.common.dto.response;

public record PendingUserResponseDto(
        String email,
        String message
) {
}