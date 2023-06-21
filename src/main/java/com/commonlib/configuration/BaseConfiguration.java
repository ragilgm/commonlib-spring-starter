package com.commonlib.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.net.BindException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Configuration
@ComponentScan("com.commonlib")
public class BaseConfiguration {

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public MapperFacade mapperFacade(){
        return new DefaultMapperFactory.Builder().build().getMapperFacade();
    }


    @Bean(name = "responseStatusMapping")
    public Map<Object, HttpStatus> statusMapping(){
        Map<Object,HttpStatus> map = new HashMap<>();
        map.put(NoSuchElementException.class,HttpStatus.NOT_FOUND);
        map.put(Exception.class,HttpStatus.INTERNAL_SERVER_ERROR);
        map.put(ValidationException.class,HttpStatus.BAD_REQUEST);
        map.put(ConstraintViolationException.class,HttpStatus.BAD_REQUEST);
        map.put(BindException.class,HttpStatus.BAD_REQUEST);
        map.put(IllegalArgumentException.class,HttpStatus.BAD_REQUEST);
        map.put(MethodArgumentNotValidException.class,HttpStatus.BAD_REQUEST);
        return map;
    }

}
