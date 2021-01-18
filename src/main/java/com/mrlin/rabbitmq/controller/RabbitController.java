package com.mrlin.rabbitmq.controller;

import com.mrlin.rabbitmq.service.RabbitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
/**
 * @Description:
 * @Author: ljm
 * @Date: 2021/1/14 11:52
 * @Version: 1.0
 */
@RestController
@RequestMapping("/rabbitmq")
@Api(tags = "Rabbit接口")
public class RabbitController {
    @Autowired
    private RabbitService rabbitService;

    @GetMapping("send")
    @ApiOperation(value = "发送")
    public  void testSend(){
        rabbitService.testSend();
    }

}
