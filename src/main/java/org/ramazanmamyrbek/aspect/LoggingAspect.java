package org.ramazanmamyrbek.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* org.ramazanmamyrbek.repository.*.*(..))")
    public void logBefore() {
        log.info("Method called: Before database interaction");
    }

    @AfterReturning("execution(* org.ramazanmamyrbek.repository.*.*(..))")
    public void logAfterReturning() {
        log.info("Method finished: After successful database interaction");
    }

    @After("execution(* org.ramazanmamyrbek.repository.*.*(..))")
    public void logAfter() {
        log.info("Method executed: After database interaction (regardless of outcome)");
    }

    @Around("execution(* org.ramazanmamyrbek.repository..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("Method {} executed in {} ms", joinPoint.getSignature(), executionTime);
        return proceed;
    }

    @AfterThrowing(pointcut = "execution(* org.ramazanmamyrbek.repository..*(..))", throwing = "exception")
    public void logAfterThrowing(Exception exception) {
        log.error("An error occurred: {}", exception.getMessage(), exception);
    }
}
