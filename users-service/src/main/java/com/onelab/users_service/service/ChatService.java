package com.onelab.users_service.service;

import org.onelab.common.dto.request.MessageRequest;
import org.onelab.common.dto.response.ChatResponseDto;
import org.onelab.common.dto.response.MessageResponseDto;

import java.security.Principal;
import java.util.List;

public interface ChatService {
    String createChat(Long senderId, Long receiverId);

    List<ChatResponseDto> getChatsByReceiverId(Principal principal);

    void saveMessage(MessageRequest message);

    void setMessagesToSeen(String chatId, Principal principal);

    List<MessageResponseDto> findChatMessages(String chatId);
}
