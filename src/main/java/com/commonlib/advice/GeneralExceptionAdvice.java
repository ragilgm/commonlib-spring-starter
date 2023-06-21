package com.commonlib.advice;

import com.commonlib.dto.DefaultAttributeErrorDto;
import com.commonlib.dto.DetailErrorDto;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GeneralExceptionAdvice berfungsi sebagai interceptor Exception yang bersifat general yang digunakan oleh banyak project Astrapay
 */
@RestControllerAdvice
@Slf4j
public class GeneralExceptionAdvice {

    private final Map<Object,HttpStatus> httpStatusMap;

    @Autowired
    public GeneralExceptionAdvice(@Qualifier("responseStatusMapping") Map<Object,HttpStatus> myBean) {
        this.httpStatusMap = myBean;
    }

    /**
     * Exception berfungsi sebagai intercept Exception.class.
     * Exception akan membentuk error yang lebih user friendly dengan format <a href="{@see DefaultAttributeError}">DefaultAttributeError</a>
     *
     * @param ex      {@link NoSuchElementException}
     * @param request {@link HttpServletRequest}
     * @return {@link DefaultAttributeErrorDto}
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public DefaultAttributeErrorDto exception(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Class<? extends Throwable> claz = ex.getClass();
        var detailMessage = ex.getMessage();
       if (!Objects.isNull(ex.getCause())){
           claz= ex.getCause().getClass();
           detailMessage=ex.getCause().getMessage();
       }

        var httpStatus = httpStatusMap.get(claz);

       if (Objects.isNull(httpStatus)){
           httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
        }

        response.setStatus(httpStatus.value());
        return DefaultAttributeErrorDto.builder()
                .timestamp(LocalDateTime.now().toString())
                .error(httpStatus.getReasonPhrase())
                .message(detailMessage)
                .status(httpStatus.value())
                .path(request.getRequestURI())
                .details(new ArrayList<>())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({BindException.class,  ConstraintViolationException.class})
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