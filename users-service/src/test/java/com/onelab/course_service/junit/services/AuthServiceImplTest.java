package com.onelab.course_service.junit.services;

import com.onelab.users_service.config.security.JwtProvider;
import com.onelab.users_service.entity.Users;
import com.onelab.users_service.service.UserService;
import com.onelab.users_service.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.request.UserLoginRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private Users testUser;
    private UserLoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .email("test@example.com")
                .passwordHash("hashed_password")
                .build();

        loginRequest = new UserLoginRequestDto("test@example.com", "password");
    }

    @Test
    void shouldLoginSuccessfully() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtProvider.generateAccessToken(testUser)).thenReturn("access_token");
        when(jwtProvider.generateRefreshToken(testUser)).thenReturn("refresh_token");

        Map<String, String> tokens = authService.login(loginRequest);

        assertThat(tokens).isNotNull();
        assertThat(tokens).containsKeys("accessToken", "refreshToken");
        assertThat(tokens.get("accessToken")).isEqualTo("access_token");
        assertThat(tokens.get("refreshToken")).isEqualTo("refresh_token");

        verify(userService, times(1)).getUserByEmail("test@example.com");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider, times(1)).generateAccessToken(testUser);
        verify(jwtProvider, times(1)).generateRefreshToken(testUser);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUser);
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(userService, times(1)).getUserByEmail("test@example.com");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider, never()).generateAccessToken(any());
        verify(jwtProvider, never()).generateRefreshToken(any());
    }

}
