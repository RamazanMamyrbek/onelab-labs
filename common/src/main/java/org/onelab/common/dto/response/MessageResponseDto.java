package org.onelab.common.dto.response;

import org.onelab.common.enums.MessageState;
import org.onelab.common.enums.MessageType;

import java.time.LocalDateTime;

public record MessageResponseDto(
        Long id,
        String content,
        MessageType messageType,
        MessageState messageState,
        Long senderId,
        Long receiverId,
        LocalDateTime createdAt,
        Long resourceId
) {
}
