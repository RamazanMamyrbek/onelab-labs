package com.onelab.course_service.junit.services;

import com.onelab.users_service.entity.Chat;
import com.onelab.users_service.entity.Message;
import com.onelab.users_service.entity.Users;
import com.onelab.users_service.mapper.ChatMapper;
import com.onelab.users_service.repository.jpa.ChatRepository;
import com.onelab.users_service.repository.jpa.MessageRepository;
import com.onelab.users_service.service.UserService;
import com.onelab.users_service.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.MessageRequest;
import org.onelab.common.dto.response.ChatResponseDto;
import org.onelab.common.dto.response.MessageResponseDto;
import org.onelab.common.enums.MessageState;
import org.onelab.common.enums.MessageType;
import org.onelab.common.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatMapper chatMapper;

    @Mock
    private Principal principal;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    void createChat_WhenChatExists_ShouldReturnExistingChatId() {
        Chat existingChat = new Chat();
        existingChat.setId("existing-id");
        when(chatRepository.findChatBySenderAndReceiver(1L, 2L)).thenReturn(Optional.of(existingChat));

        String result = chatService.createChat(1L, 2L);

        assertEquals("existing-id", result);
        verify(chatRepository).findChatBySenderAndReceiver(1L, 2L);
        verifyNoMoreInteractions(chatRepository, userService);
    }

    @Test
    void createChat_WhenChatNotExists_ShouldCreateNewChat() {
        when(chatRepository.findChatBySenderAndReceiver(1L, 2L)).thenReturn(Optional.empty());
        Users sender = new Users();
        sender.setId(1L);
        Users receiver = new Users();
        receiver.setId(2L);
        when(userService.getUserById(1L)).thenReturn(sender);
        when(userService.getUserById(2L)).thenReturn(receiver);
        Chat newChat = new Chat();
        newChat.setId("new-id");
        when(chatRepository.save(any(Chat.class))).thenReturn(newChat);

        String result = chatService.createChat(1L, 2L);

        assertEquals("new-id", result);
        verify(chatRepository).save(any(Chat.class));
    }

    @Test
    void getChatsByReceiverId_ShouldReturnChats() {
        Long userId = 1L;
        when(principal.getName()).thenReturn("user@test.com");
        Users user = new Users();
        user.setId(userId);
        when(userService.getUserByEmail("user@test.com")).thenReturn(user);
        Chat chat = new Chat();
        when(chatRepository.findChatsBySenderId(userId)).thenReturn(List.of(chat));
        ChatResponseDto dto = new ChatResponseDto("id", "name", 1L, 2L);
        when(chatMapper.mapToChatResponseDto(chat, userId)).thenReturn(dto);

        List<ChatResponseDto> result = chatService.getChatsByReceiverId(principal);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void saveMessage_ShouldSaveMessage() {
        MessageRequest request = new MessageRequest("",1L, 2L, MessageType.TEXT, "chat-id");
        Chat chat = new Chat();
        when(chatRepository.findById("chat-id")).thenReturn(Optional.of(chat));

        chatService.saveMessage(request);

        verify(messageRepository).save(any(Message.class));
    }


    @Test
    void findChatMessages_ShouldReturnMessages() {
        Message message = new Message();
        when(messageRepository.findMessagesByChatId("chat-id")).thenReturn(List.of(message));
        MessageResponseDto dto = new MessageResponseDto(1L, "content",  MessageType.TEXT, MessageState.SENT, 1l, 1l, LocalDateTime.now(), 1L);
        when(chatMapper.toMessageResponse(message)).thenReturn(dto);

        List<MessageResponseDto> result = chatService.findChatMessages("chat-id");

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

}