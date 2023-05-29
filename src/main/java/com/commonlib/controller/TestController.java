//package com.commonlib.controller;
//
//import com.commonlib.annotation.LogExecutionTime;
//import com.commonlib.annotation.LogRequestResponse;
//import com.commonlib.annotation.RacingConditionRejection;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/test")
//public class TestController {
//
//    @GetMapping
//    @LogExecutionTime
//    @LogRequestResponse
//    @RacingConditionRejection
//    public String getTest(){
//        return "hello world";
//    }
//
//}
