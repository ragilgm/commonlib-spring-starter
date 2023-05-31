package com.commonlib.controller;

import com.commonlib.annotation.LogExecutionTime;
import com.commonlib.annotation.LogRequestResponse;
import com.commonlib.annotation.RacingConditionRejection;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/test")
@Validated
public class TestController {

    @GetMapping
    @LogExecutionTime
    @LogRequestResponse
    @RacingConditionRejection
    public String getTest( @NotNull String name){
        throw new NoSuchElementException("not found boss");
    }

}
