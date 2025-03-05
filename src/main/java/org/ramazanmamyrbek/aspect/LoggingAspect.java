package org.ramazanmamyrbek.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* org.ramazanmamyrbek.services.SchoolService.removeAllData(..))")
    public void removeAllDataPointcut() {}

    @Around("removeAllDataPointcut()")
    public Object logBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Удаление данных начато: {}()",joinPoint.getSignature().getName());
        Object proceed = joinPoint.proceed();
        log.info("Удаление данных завершено: {}()",joinPoint.getSignature().getName());
        return proceed;
    }
}
