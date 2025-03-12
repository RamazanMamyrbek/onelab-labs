package com.onelab.users_service.service.impl;

import com.onelab.users_service.config.security.JwtProvider;
import com.onelab.users_service.entity.Users;
import com.onelab.users_service.service.AuthService;
import com.onelab.users_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.onelab.common.dto.request.UserLoginRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Override
    public Map<String, String> login(UserLoginRequestDto requestDto) {
        Map<String, String> map = new HashMap<>();
        Users user = userService.getUserByEmail(requestDto.email());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user,
                requestDto.password()
        ));
        map.put("accessToken", jwtProvider.generateAccessToken(user));
        map.put("refreshToken", jwtProvider.generateRefreshToken(user));
        return map;
    }
}
