package com.onelab.course_service.junit.controller;


import com.onelab.users_service.config.security.JwtProvider;
import com.onelab.users_service.config.security.UserDetailsServiceImpl;
import com.onelab.users_service.controller.JwtController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onelab.common.dto.response.UserDetailsResponseDto;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class JwtControllerTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private JwtController jwtController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jwtController).build();
    }

    @Test
    void shouldValidateTokenSuccessfully() throws Exception {
        String token = "test-token";
        when(jwtProvider.validateToken(token)).thenReturn(true);

        mockMvc.perform(get("/jwt/validate")
                        .param("token", token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLoadUserByUsernameSuccessfully() throws Exception {
        String email = "user@example.com";
        UserDetails userDetails = new User(email, "hashed_password", List.of((GrantedAuthority) () -> "ROLE_USER"));
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        mockMvc.perform(get("/jwt/load-user-by-email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(email))
                .andExpect(jsonPath("$.password").value("hashed_password"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"));
    }

    @Test
    void shouldExtractEmailFromTokenSuccessfully() throws Exception {
        String token = "test-token";
        String expectedEmail = "user@example.com";
        when(jwtProvider.getEmail(token)).thenReturn(expectedEmail);

        mockMvc.perform(get("/jwt/extract-email")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedEmail));
    }
}

