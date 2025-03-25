package com.onelab.course_service.junit.controller;



import com.onelab.users_service.controller.AuthController;
import com.onelab.users_service.service.AuthService;
import com.onelab.users_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onelab.common.dto.request.ConfirmEmailRequestDto;
import org.onelab.common.dto.request.UserLoginRequestDto;
import org.onelab.common.dto.request.UserRegisterRequestDto;
import org.onelab.common.dto.response.PendingUserResponseDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Role;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldReturnCreatedResponse() {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto(
                "test@example.com", "password123", "John Doe", "USA", 25L, Role.ROLE_TEACHER
        );
        PendingUserResponseDto responseDto = new PendingUserResponseDto("test@example.com", "123456");

        when(userService.registerUser(requestDto)).thenReturn(responseDto);

        ResponseEntity<PendingUserResponseDto> response = authController.registerUser(requestDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
        verify(userService, times(1)).registerUser(requestDto);
    }

    @Test
    void confirmEmail_ShouldReturnUserResponse() {
        ConfirmEmailRequestDto requestDto = new ConfirmEmailRequestDto("test@example.com", "123456");
        UsersResponseDto responseDto = new UsersResponseDto(1L, "test@example.com", "John Doe", "USA", 25L, "USER", BigDecimal.valueOf(100.0), "USD");

        when(userService.confirmEmail(requestDto)).thenReturn(responseDto);

        ResponseEntity<UsersResponseDto> response = authController.confirmEmail(requestDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
        verify(userService, times(1)).confirmEmail(requestDto);
    }

    @Test
    void resendCode_ShouldReturnOkResponse() {
        String email = "test@example.com";

        ResponseEntity<Map<String, String>> response = authController.resendCode(email);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Map.of("message", "Confirmation code was sent"), response.getBody());
        verify(userService, times(1)).resendCode(email);
    }

    @Test
    void login_ShouldReturnTokenResponse() {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("test@example.com", "password123");
        Map<String, String> tokenResponse = Map.of("token", "mocked_token");

        when(authService.login(requestDto)).thenReturn(tokenResponse);

        ResponseEntity<Map<String, String>> response = authController.login(requestDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tokenResponse, response.getBody());
        verify(authService, times(1)).login(requestDto);
    }
}