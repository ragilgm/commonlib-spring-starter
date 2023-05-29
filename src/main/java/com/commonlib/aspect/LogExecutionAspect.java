package com.commonlib.aspect;

import com.commonlib.configuration.CachedBodyHttpServletRequestConfiguration;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
public class LogExecutionAspect {

    @Autowired
    private HttpServletRequest servletRequest;

    public static final Integer PARAMETER_EXCEPTION = 0;
    public static final Integer PARAMETER_REQUEST = 1;

    @Around("@annotation(com.commonlib.annotation.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");
        return proceed;
    }

    @Around("@annotation(com.commonlib.annotation.LogRequestResponseAdvice)")
    public Object logRequestResponseAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] request = joinPoint.getArgs();
        Object response = joinPoint.proceed();
        try {
            String controller, controllerMethod, uri;
            uri = servletRequest.getRequestURI();
            controller = joinPoint.getSignature().getDeclaringTypeName();
            controllerMethod = joinPoint.getSignature().getName();
            Exception exception = (Exception) request[PARAMETER_EXCEPTION];
            HttpServletRequest httpServletRequest = (HttpServletRequest) request[PARAMETER_REQUEST];
            CachedBodyHttpServletRequestConfiguration cachedBodyHttpServletRequest = (CachedBodyHttpServletRequestConfiguration) httpServletRequest;
            log.error(new ObjectAppendingMarker("uri", uri),
                    "Entering " + controller + "." + controllerMethod + "()",
                    new ObjectAppendingMarker("request", cachedBodyHttpServletRequest.getServletDto()),
                    new ObjectAppendingMarker("response", response),
                    new ObjectAppendingMarker("exception", exception.getMessage()));
            return response;
        } catch (Exception e) {
            log.error("Error logging Request-Response Advice cause ", e);
            return response;
        }
    }

    @Around("@annotation(com.commonlib.annotation.LogRequestResponse)")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object request, response;
        request = joinPoint.getArgs();
        response = joinPoint.proceed();
        try {
            String controller, controllerMethod, uri;
            controller = joinPoint.getSignature().getDeclaringTypeName();
            controllerMethod = joinPoint.getSignature().getName();
            uri = servletRequest.getRequestURI();
            log.info(new ObjectAppendingMarker("uri", uri),
                    "Entering " + controller + "." + controllerMethod + "()",
                    new ObjectAppendingMarker("request", request),
                    new ObjectAppendingMarker("response", response));
            return response;
        } catch (Exception e) {
            log.error("Error logging Request-Response cause ", e);
            return response;
        }
    }
}
