package com.plugin.info.controller;

import com.plugin.info.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testR")
public class TestRestController {

    @Autowired
    private TestService testService;

    @GetMapping("/timeA")
    public String getTime(){
        return testService.testTime();
    }



}
