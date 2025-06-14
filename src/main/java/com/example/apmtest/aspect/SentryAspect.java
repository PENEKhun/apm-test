package com.example.apmtest.aspect;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class SentryAspect {

    @Pointcut("execution(* com.example.apmtest.service.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.example.apmtest.controller.*.*(..))")
    public void controllerLayer() {}

    @Around("serviceLayer() || controllerLayer()")
    public Object captureExceptionWithArguments(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            // Get the method name for logging
            String methodName = joinPoint.getSignature().toShortString();

            // Check if we're in a try-catch block that already calls Sentry.captureException
            // This is a heuristic to avoid duplicate events - we check the stack trace
            boolean alreadyHandledBySentry = isExceptionAlreadyHandledBySentry();

            if (!alreadyHandledBySentry) {
                // Capture method arguments
                Map<String, Object> methodArgs = captureMethodArguments(joinPoint);

                // Create a Sentry event with the exception
                SentryEvent event = new SentryEvent(throwable);
                event.setLevel(SentryLevel.ERROR);

                // Add method arguments as extra data
                for (Map.Entry<String, Object> entry : methodArgs.entrySet()) {
                    event.setExtra(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "null");
                }

                // Add method name information
                event.setExtra("method", methodName);

                // Set a message that includes the method name
                Message message = new Message();
                message.setMessage("Exception in " + methodName);
                event.setMessage(message);

                // Send the event to Sentry
                Sentry.captureEvent(event);
            }

            // Re-throw the exception
            throw throwable;
        }
    }

    /**
     * Check if the exception is already being handled by Sentry
     * This is a heuristic - we check if Sentry.captureException appears in the stack trace
     */
    private boolean isExceptionAlreadyHandledBySentry() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            // Check if we're in a catch block that calls Sentry.captureException
            if (element.getClassName().contains("com.example.apmtest") && 
                !element.getClassName().equals(this.getClass().getName())) {
                // We're in application code, but not in this aspect
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> captureMethodArguments(JoinPoint joinPoint) {
        Map<String, Object> argsMap = new HashMap<>();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] parameterNames = signature.getParameterNames(); // This will get the parameter names if available
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            String paramName = parameterNames[i];
            Object argValue = i < args.length ? args[i] : null;

//            if (!paramName.toLowerCase().contains("password") && !paramName.toLowerCase().contains("token")) {
//                scope.setExtra("arg_" + paramName, "[마스킹 처리됨]");
//            }

            // Add both the parameter name and its value
            argsMap.put("arg_" + paramName, argValue);

            // Also add the parameter type for more context
            if (argValue != null) {
                argsMap.put("type_" + paramName, argValue.getClass().getSimpleName());
            }
        }

        return argsMap;
    }
}
