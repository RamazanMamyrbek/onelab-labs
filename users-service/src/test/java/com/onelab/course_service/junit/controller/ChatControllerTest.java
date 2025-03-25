package com.onelab.course_service.junit.controller;


import com.onelab.users_service.controller.ChatController;
import com.onelab.users_service.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onelab.common.dto.request.MessageRequest;
import org.onelab.common.dto.response.ChatResponseDto;
import org.onelab.common.dto.response.MessageResponseDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createChat_ShouldReturnChatId() {
        Long senderId = 1L;
        Long receiverId = 2L;
        String chatId = "chat123";

        when(chatService.createChat(senderId, receiverId)).thenReturn(chatId);

        ResponseEntity<Map<String, String>> response = chatController.createChat(senderId, receiverId);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(Map.of("chatId", chatId), response.getBody());
        verify(chatService, times(1)).createChat(senderId, receiverId);
    }

    @Test
    void getChatsByReceiver_ShouldReturnChatList() {
        Principal principal = mock(Principal.class);
        List<ChatResponseDto> chats = List.of(new ChatResponseDto("chat123", "Chat Name", 1L, 2L));

        when(chatService.getChatsByReceiverId(principal)).thenReturn(chats);

        ResponseEntity<List<ChatResponseDto>> response = chatController.getChatsByReceiver(principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(chats, response.getBody());
        verify(chatService, times(1)).getChatsByReceiverId(principal);
    }

    @Test
    void saveMessage_ShouldReturnCreatedStatus() {
        MessageRequest message = new MessageRequest("Hello", 1L, 2L, null, "chat123");

        ResponseEntity<Void> response = chatController.saveMessage(message);

        assertEquals(201, response.getStatusCodeValue());
        verify(chatService, times(1)).saveMessage(message);
    }

    @Test
    void setMessagesToSeen_ShouldReturnAcceptedStatus() {
        String chatId = "chat123";
        Principal principal = mock(Principal.class);

        ResponseEntity<Void> response = chatController.setMessagesToSeen(chatId, principal);

        assertEquals(202, response.getStatusCodeValue());
        verify(chatService, times(1)).setMessagesToSeen(chatId, principal);
    }

    @Test
    void getMessages_ShouldReturnMessageList() {
        String chatId = "chat123";
        List<MessageResponseDto> messages = List.of(
                new MessageResponseDto(1L, "Hello", null, null, 1L, 2L, LocalDateTime.now(), null)
        );

        when(chatService.findChatMessages(chatId)).thenReturn(messages);

        ResponseEntity<List<MessageResponseDto>> response = chatController.getMessages(chatId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(messages, response.getBody());
        verify(chatService, times(1)).findChatMessages(chatId);
    }
}