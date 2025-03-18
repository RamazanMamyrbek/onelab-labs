package com.onelab.course_service.junit.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.onelab.users_service.controller.AuthController;
import com.onelab.users_service.service.AuthService;
import com.onelab.users_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.UserLoginRequestDto;
import org.onelab.common.dto.request.UserRegisterRequestDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Role;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto("john@example.com", "password123", "John Doe","Kazakhstan", 27L, Role.ROLE_STUDENT);
        UsersResponseDto responseDto = new UsersResponseDto(1L,  "john@example.com", "John Doe","Kazakhstan", 27L, Role.ROLE_STUDENT.name());

        when(userService.registerUser(any(UserRegisterRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("john@example.com", "password123");
        Map<String, String> tokens = Map.of(
                "accessToken", "access_token_value",
                "refreshToken", "refresh_token_value"
        );

        when(authService.login(any(UserLoginRequestDto.class))).thenReturn(tokens);

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token_value"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token_value"));
    }
}
