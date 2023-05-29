package com.commonlib.controller;

import com.commonlib.annotation.LogExecutionTime;
import com.commonlib.annotation.LogRequestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @LogExecutionTime
    @LogRequestResponse
    public String getTest(){
        return "hello world";
    }

}
