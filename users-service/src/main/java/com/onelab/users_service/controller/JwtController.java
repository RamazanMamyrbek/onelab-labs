package com.onelab.users_service.controller;

import com.onelab.users_service.config.security.JwtProvider;
import com.onelab.users_service.config.security.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.onelab.common.dto.response.UserDetailsResponseDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Jwt controller")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/jwt")
@Validated
@Hidden
public class JwtController {
    final JwtProvider jwtProvider;
    final UserDetailsServiceImpl userDetailsService;

    @GetMapping(path = "/validate")
    @Operation(summary = "Validate token", description = "Endpoint for jwt validation for other microservices.")
    public boolean validateToken(@RequestParam(name = "token") String token) {
        return jwtProvider.validateToken(token);
    }

    @GetMapping(path = "/load-user-by-email")
    @Operation(summary = "Load user by email")
    public UserDetailsResponseDto loadUserByUsername(@RequestParam(name = "email") String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        List<String> responseAuthorities = userDetails.getAuthorities().stream().map(element -> element.getAuthority()).toList();
        return new UserDetailsResponseDto(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.isEnabled(),
                responseAuthorities
        );
    }

    @GetMapping(path = "/extract-email")
    public String getEmail(@RequestParam(name = "token") String token) {
        return jwtProvider.getEmail(token);
    }
}

