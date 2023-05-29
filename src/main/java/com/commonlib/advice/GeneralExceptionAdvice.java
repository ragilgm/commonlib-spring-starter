package com.commonlib.advice;

import com.commonlib.dto.DefaultAttributeErrorDto;
import com.commonlib.dto.DetailErrorDto;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GeneralExceptionAdvice berfungsi sebagai interceptor Exception yang bersifat general yang digunakan oleh banyak project Astrapay
 */
@RestControllerAdvice()
@Slf4j
public class GeneralExceptionAdvice {



    /**
     * Exception berfungsi sebagai intercept Exception.class.
     * Exception akan membentuk error yang lebih user friendly dengan format <a href="{@see DefaultAttributeError}">DefaultAttributeError</a>
     *
     * @param ex      {@link BindException}
     * @param request {@link HttpServletRequest}
     * @return {@link DefaultAttributeErrorDto}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public DefaultAttributeErrorDto Exception(Exception ex, HttpServletRequest request) {
        log.error("GeneralExceptionAdvice::" + ex.getClass(), ex);

        return DefaultAttributeErrorDto.builder()
                .timestamp(LocalDateTime.now().toString())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .details(new ArrayList<>())
                .build();
    }

    /**
     * BindException berfungsi sebagai intercept BindException.class.
     * BindException akan membentuk error yang lebih user friendly dengan format <a href="{@see DefaultAttributeError}">DefaultAttributeError</a>
     *
     * @param ex      {@link BindException}
     * @param request {@link HttpServletRequest}
     * @return {@link DefaultAttributeErrorDto}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({BindException.class, ConstraintViolationException.class})
    public DefaultAttributeErrorDto BindException(BindException ex, HttpServletRequest request) {
        log.error("GeneralExceptionAdvice::" + ex.getClass(), ex);

        BindingResult result = ex.getBindingResult();
        List<ObjectError> objectErrorList = result.getAllErrors();
        
        List<String> propertyPaths = new ArrayList<>();
        List<DetailErrorDto> detailErrorList = objectErrorList.stream().map(error -> {
                    ConstraintViolationImpl<?> constraintViolation = error.unwrap(ConstraintViolationImpl.class);
                    String propertyPath = constraintViolation.getPropertyPath().toString();
                    propertyPaths.add(propertyPath);
                    return DetailErrorDto.builder()
                            .field(propertyPath)
                            .rejectedValue(constraintViolation.getInvalidValue())
                            .objectName(error.getObjectName())
                            .code(error.getCode())
                            .defaultMessage(error.getDefaultMessage())
                            .build();
                })
                .collect(Collectors.toList());

        return DefaultAttributeErrorDto.builder()
                .timestamp(LocalDateTime.now().toString())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed for " + ex.getObjectName() + "(" + StringUtils.join(propertyPaths, ",") + ")" + ". Error count " + ex.getErrorCount())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .details(detailErrorList)
                .build();
    }
}
