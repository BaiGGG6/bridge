package com.plugin.info.service;

import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TestService {


    public String testTime(){
        return JSONUtil.toJsonStr(LocalDateTime.now());
    }

}
