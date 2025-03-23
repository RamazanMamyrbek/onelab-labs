package com.onelab.users_service.service;

public interface EmailService {
    void sendConfirmationCode(String email, String code);

}
