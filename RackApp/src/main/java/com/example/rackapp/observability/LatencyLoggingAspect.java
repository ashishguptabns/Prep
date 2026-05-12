package com.example.rackapp.observability;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LatencyLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LatencyLoggingAspect.class);

    @Around("@annotation(com.example.rackapp.observability.LogLatency) || @within(com.example.rackapp.observability.LogLatency)")
    public Object logLatency(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNanos = System.nanoTime();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        try {
            Object result = joinPoint.proceed();
            log.info("{} completed in {} ms", methodName, elapsedMillis(startNanos));
            return result;
        } catch (Throwable ex) {
            log.warn("{} failed after {} ms", methodName, elapsedMillis(startNanos), ex);
            throw ex;
        }
    }

    private long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
