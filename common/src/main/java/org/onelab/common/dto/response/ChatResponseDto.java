package org.onelab.common.dto.response;

public record ChatResponseDto(
        String id,
        String name,
        Long senderId,
        Long receiverId
) {
}
