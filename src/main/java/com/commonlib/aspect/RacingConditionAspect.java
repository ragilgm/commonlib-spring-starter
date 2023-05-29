package com.commonlib.aspect;

import com.commonlib.dto.ServletDto;
import com.commonlib.service.CustomServletService;
import com.commonlib.service.SignatureGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class RacingConditionAspect {


    @Autowired
    private CustomServletService customServletService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SignatureGeneratorService signatureGeneratorService;


    private static final String SEPARATOR_COLON = "::";

    @Around("@annotation(com.commonlib.annotation.RacingConditionRejection)")
    public Object recingConditionIntercept(ProceedingJoinPoint joinPoint) throws Throwable {
       ServletDto servletDto =  customServletService.getServletDto();
        var key = buildKey(servletDto);
        var isAllowed = false;
        try {

            long count = redisTemplate.opsForValue().increment(key, 1);
            log.debug("count : {}", count);

            if (count == 1) {
                redisTemplate.expire(key, 10, TimeUnit.SECONDS);
                isAllowed=true;
            }

        }catch (Exception e){
            log.error("Error ",e);
            isAllowed= true;
        }

        if (!isAllowed){
            throw new IllegalArgumentException("Request not allowed");
        }

        return joinPoint.proceed();
    }

    private String buildKey(ServletDto servletDto) throws NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder stringBuilder=  new StringBuilder();
        stringBuilder.append(servletDto.getMethod());
        stringBuilder.append(SEPARATOR_COLON);
        stringBuilder.append(servletDto.getUri());
        stringBuilder.append(servletDto.getParams());

        signatureGeneratorService.createSignature(stringBuilder.toString());
        return stringBuilder.toString();
    }


}