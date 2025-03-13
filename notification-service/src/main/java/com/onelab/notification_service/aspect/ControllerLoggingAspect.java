package com.onelab.notification_service.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Pointcut("within(com.onelab.notification_service.controller..*)")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous";

        log.info("Incoming Request: [{}] {} from IP: {}, User: {}, Args: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                username,
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "response")
    public void logResponse(JoinPoint joinPoint, Object response) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        if (response instanceof ResponseEntity<?> responseEntity) {
            log.info("Outgoing Response: [{}] {} -> {} (Status: {})",
                    request.getMethod(),
                    request.getRequestURI(),
                    signature.getMethod().getName(),
                    responseEntity.getStatusCode());
        } else {
            log.info("Outgoing Response: [{}] {} -> {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    signature.getMethod().getName());
        }
    }
}

