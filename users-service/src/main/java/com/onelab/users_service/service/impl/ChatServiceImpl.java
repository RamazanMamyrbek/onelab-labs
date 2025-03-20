package com.onelab.users_service.service.impl;

import com.onelab.users_service.entity.Chat;
import com.onelab.users_service.entity.Message;
import com.onelab.users_service.entity.Users;
import com.onelab.users_service.mapper.ChatMapper;
import com.onelab.users_service.repository.jpa.ChatRepository;
import com.onelab.users_service.repository.jpa.MessageRepository;
import com.onelab.users_service.service.ChatService;
import com.onelab.users_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.MessageRequest;
import org.onelab.common.dto.response.ChatResponseDto;
import org.onelab.common.dto.response.MessageResponseDto;
import org.onelab.common.enums.MessageState;
import org.onelab.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatMapper chatMapper;

    @Override
    @Transactional
    public String createChat(Long senderId, Long receiverId) {
        Optional<Chat> chat = chatRepository.findChatBySenderAndReceiver(senderId, receiverId);
        if(chat.isPresent()) {
            return chat.get().getId();
        }
        Users sender = userService.getUserById(senderId);
        Users receiver = userService.getUserById(receiverId);
        Chat newChat = Chat
                .builder()
                .sender(sender)
                .receiver(receiver)
                .build();
        newChat = chatRepository.save(newChat);
        return newChat.getId();
    }

    @Override
    public List<ChatResponseDto> getChatsByReceiverId(Principal principal) {
        Long userId = userService.getUserByEmail(principal.getName()).getId();
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map(chat -> chatMapper.mapToChatResponseDto(chat, userId))
                .toList();
    }

    @Override
    @Transactional
    public void saveMessage(MessageRequest messageRequest) {
        Chat chat = getChatById(messageRequest.chatId());
        Message message = Message
                .builder()
                .content(messageRequest.content())
                .chat(chat)
                .senderId(messageRequest.senderId())
                .receiverId(messageRequest.receiverId())
                .messageType(messageRequest.messageType())
                .messageState(MessageState.SENT)
                .build();
        messageRepository.save(message);
    }


    @Override
    public void setMessagesToSeen(String chatId, Principal principal) {
        Chat chat = getChatById(chatId);
        Long receiverId = getReceiverId(chat, principal);
        messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);
    }



    @Override
    public List<MessageResponseDto> findChatMessages(String chatId) {
        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(chatMapper::toMessageResponse)
                .toList();
    }

    private Chat getChatById(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> ResourceNotFoundException.chatNotFound(chatId));
    }

    private Long getSenderId(Chat chat, Principal principal) {
        Users user = userService.getUserByEmail(principal.getName());
        if(chat.getSender().getId().equals(user.getId())) {
            return chat.getSender().getId();
        }
        return chat.getReceiver().getId();
    }

    private Long getReceiverId(Chat chat, Principal principal) {
        Users user = userService.getUserByEmail(principal.getName());
        if(chat.getSender().getId().equals(user.getId())) {
            return chat.getReceiver().getId();
        }
        return chat.getSender().getId();
    }
}
