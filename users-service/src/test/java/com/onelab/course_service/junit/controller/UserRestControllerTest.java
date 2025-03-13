package com.onelab.course_service.junit.controller;

import com.onelab.users_service.controller.UserRestController;
import com.onelab.users_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.UserEditRequestDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Role;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build();
    }

    @Test
    void shouldReturnAllUsersSuccessfully() throws Exception {
        List<UsersResponseDto> users = List.of(
                new UsersResponseDto(1L, "user1@example.com", "User One", Role.ROLE_STUDENT.name()),
                new UsersResponseDto(2L, "user2@example.com", "User Two", Role.ROLE_STUDENT.name())
        );

        when(userService.getAllUsers(null)).thenReturn(users);

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }

    @Test
    void shouldReturnUserInfoSuccessfully() throws Exception {
        UsersResponseDto user = new UsersResponseDto(1L, "user@example.com", "User One", Role.ROLE_STUDENT.name());

        when(userService.getUserInfoById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.name").value("User One"));
    }

    @Test
    void shouldReturnProfileInfoSuccessfully() throws Exception {
        String username = "user@example.com";
        Principal principal = () -> username;
        UsersResponseDto user = new UsersResponseDto(1L, username, "User One", Role.ROLE_STUDENT.name());

        when(userService.getUserProfileByEmail(username)).thenReturn(user);

        mockMvc.perform(get("/api/users/profile")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value(username))
                .andExpect(jsonPath("$.name").value("User One"));
    }


    @Test
    void shouldDeleteProfileSuccessfully() throws Exception {
        String username = "user@example.com";
        Principal principal = () -> username;

        mockMvc.perform(delete("/api/users")
                        .principal(principal))
                .andExpect(status().isNoContent());
    }
}

