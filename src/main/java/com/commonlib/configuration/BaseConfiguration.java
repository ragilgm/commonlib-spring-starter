package com.commonlib.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.commonlib")
public class BaseConfiguration {

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }


}
