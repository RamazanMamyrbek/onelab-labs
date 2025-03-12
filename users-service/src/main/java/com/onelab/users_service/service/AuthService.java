package com.onelab.users_service.service;

import org.onelab.common.dto.request.UserLoginRequestDto;

import java.util.Map;

public interface AuthService {
    Map<String, String> login(UserLoginRequestDto requestDto);
}
