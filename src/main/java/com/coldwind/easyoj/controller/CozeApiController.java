package com.coldwind.easyoj.controller;

import com.coldwind.easyoj.service.CozeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coze")
public class CozeApiController {

    @Autowired
    private CozeApiService cozeApiService;

    @GetMapping("/bot-info")
    public String getBotInfo() {
        return cozeApiService.getBotOnlineInfo();
    }
} 