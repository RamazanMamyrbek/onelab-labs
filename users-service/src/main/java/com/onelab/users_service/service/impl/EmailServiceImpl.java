package com.onelab.users_service.service.impl;

import com.onelab.users_service.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendConfirmationCode(String email, String code) {
        try {
            String subject = "Confirm registration";
            sendHtmlConfirmationCode(subject,email, code);
        }catch (Exception e) {
            handleMailException(e);
        }
    }

    private void sendHtmlConfirmationCode(String subject, String email, String code) throws MessagingException {
        Context context = new Context();
        context.setVariable("code", code);
        String templateName = "confirm-registration";
        sendHtmlMessage(subject, email, context, templateName);
    }

    private void sendHtmlMessage(String subject, String toEmail, Context context, String templateName) throws MessagingException {
        String process = templateEngine.process(templateName, context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(process, true);
        javaMailSender.send(mimeMessageHelper.getMimeMessage());
    }

    private void handleMailException(Exception ex) {
        log.error("Error during sending email: {}", ex.getMessage());
    }

}
