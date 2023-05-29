package com.commonlib.service;

import com.commonlib.dto.ServletDto;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Objects;

@Service
@RequestScope
public class CustomServletService {

    private ServletDto servletDto;


    public ServletDto setServletDto(ServletDto servletDto){
        if (Objects.isNull(this.servletDto)){
            this.servletDto=servletDto;
        }
        return servletDto;
    }

    public ServletDto getServletDto(){
        if (Objects.isNull(this.servletDto)){
            this.servletDto=new ServletDto();
        }
        return servletDto;
    }


}
