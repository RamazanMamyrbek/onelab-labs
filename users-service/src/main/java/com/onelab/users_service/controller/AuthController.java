package com.onelab.users_service.controller;

import com.onelab.users_service.service.AuthService;
import com.onelab.users_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.UserLoginRequestDto;
import org.onelab.common.dto.request.UserRegisterRequestDto;
import org.onelab.common.dto.response.UsersResponseDto;
import org.onelab.common.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/auth")
@Tag(name = "AuthController")
@Validated
public class AuthController {
    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/register")
    @Operation(summary = "Register a user")
    public ResponseEntity<UsersResponseDto> registerUser(@RequestBody @Valid UserRegisterRequestDto requestDto) {
        UsersResponseDto responseDto = userService.registerUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        Map<String, String> responseDto = authService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
