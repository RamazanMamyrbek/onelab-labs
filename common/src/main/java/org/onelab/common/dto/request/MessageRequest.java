package org.onelab.common.dto.request;

import org.onelab.common.enums.MessageType;

public record MessageRequest(
        String content,
        Long senderId,
        Long receiverId,
        MessageType messageType,
        String chatId
) {
}
