package com.onelab.notification_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.onelab.notification_service.service.NotificationService.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("Method {} called with args: {}", methodName, args);

        try {
            Object result = joinPoint.proceed();
            log.info("Method {} returned: {}", methodName, result);
            return result;
        } catch (Exception e) {
            log.error("Method {} threw an exception: {}", methodName, e.getMessage(), e);
            throw e;
        }
    }
}

