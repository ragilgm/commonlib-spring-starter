package com.commonlib.advice;

import com.commonlib.dto.DefaultAttributeErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;

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
       if (!Objects.isNull(ex.getCause())){
           claz= ex.getCause().getClass();
       }

        var httpStatus = httpStatusMap.get(claz);

       if (Objects.isNull(httpStatus)){
           httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
        }

        response.setStatus(httpStatus.value());
        return DefaultAttributeErrorDto.builder()
                .timestamp(LocalDateTime.now().toString())
                .error(httpStatus.getReasonPhrase())
                .message(ex.getLocalizedMessage())
                .status(httpStatus.value())
                .path(request.getRequestURI())
                .details(new ArrayList<>())
                .build();
    }
}