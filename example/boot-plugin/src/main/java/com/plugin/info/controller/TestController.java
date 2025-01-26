package com.plugin.info.controller;

import com.plugin.info.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/testC")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/timeA")
    public String getTime(Model model){
        model.addAttribute("time", testService.testTime());
        return "helloPage";
    }



}
