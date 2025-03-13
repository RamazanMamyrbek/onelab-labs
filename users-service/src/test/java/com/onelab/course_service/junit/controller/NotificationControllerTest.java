package com.onelab.course_service.junit.controller;


import com.onelab.users_service.controller.NotificationController;
import com.onelab.users_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.response.NotificationResponseDto;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void shouldReturnNotificationsSuccessfully() throws Exception {
        String username = "user@example.com";
        String token = "Bearer test-token";
        Principal principal = () -> username;

        List<NotificationResponseDto> notifications = List.of(
                new NotificationResponseDto(1L, "Message 1", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)),
                new NotificationResponseDto(2L, "Message 2", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
        );

        when(userService.getNotifications(username, token)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications")
                        .principal(principal)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Message 1"))
                .andExpect(jsonPath("$[1].message").value("Message 2"));
    }


}

