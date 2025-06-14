package com.example.apmtest.aspect;

import io.sentry.ISpan;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
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
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        String spanName = className + "." + methodName;

        var parentSpan = Sentry.getSpan();
        if (parentSpan != null) {
            ISpan span = parentSpan.startChild(spanName, "AOP-traced service call");
            try {
                span.setDescription("Called " + spanName);

                Object[] args = joinPoint.getArgs();
                String[] parameterNames = signature.getParameterNames();

                for (int i = 0; i < args.length; i++) {
                    String paramName = (parameterNames != null && i < parameterNames.length && parameterNames[i] != null)
                            ? parameterNames[i]
                            : "arg" + i;

                    Object value = args[i];
                    if (paramName.toLowerCase().contains("password") || paramName.toLowerCase().contains("token")) {
                        span.setData("arg_" + paramName, "[마스킹 처리됨]");
                    } else {
                        span.setData("arg_" + paramName, String.valueOf(value));
                    }
                }

                Object result = joinPoint.proceed();
                span.setStatus(SpanStatus.OK);
                return result;
            } catch (Throwable ex) {
                span.setStatus(SpanStatus.INTERNAL_ERROR);
                throw ex;
            } finally {
                span.finish();
            }
        }

        // Sentry 트레이싱 컨텍스트 없을 경우 fallback
        return joinPoint.proceed();
    }
}
