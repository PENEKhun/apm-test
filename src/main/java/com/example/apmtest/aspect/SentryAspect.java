package com.example.apmtest.aspect;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class SentryAspect {

    @Around("execution(* com.example.apmtest.service.*.*(..))")
    public Object logArgumentsToSentry(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[SentryAspect] AOP 적용됨 - {}", joinPoint.getSignature());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();

        log.info("Method: {}", method.getName());
        log.info("args.length: {}", args.length);
        log.info("parameterNames.length: {}", parameterNames != null ? parameterNames.length : "null");

        Sentry.configureScope(scope -> {
            for (int i = 0; i < args.length; i++) {
                String name = (parameterNames != null && parameterNames.length > i && parameterNames[i] != null)
                        ? parameterNames[i]
                        : "arg" + i;

                Object value = args[i];

                try {
                    // toString() 오류 방지
                    String safeString = (value != null) ? String.valueOf(value) : "null";
                    log.info("Captured arg {} = {}", name, safeString);

                    if (!name.toLowerCase().contains("password") && !name.toLowerCase().contains("token")) {
                        scope.setExtra("arg_" + name, safeString);
                    } else {
                        scope.setExtra("arg_" + name, "[마스킹 처리됨]");
                    }
                } catch (Exception e) {
                    log.warn("❗ 예외 발생 - 파라미터 [{}]는 toString 중 오류 발생: {}", name, e.toString());
                }
            }

        });

        return joinPoint.proceed(); // 원래 메서드 실행
    }
}
