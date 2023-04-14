package cn.com.agent.controller;

import cn.com.agent.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.LockSupport;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/14 13:09
 * @Version 1.0
 */

@RestController
public class MyController {
    @Autowired
    private MyService myService;

    @GetMapping("/testagent")
    public String testAgent() {
        LockSupport.parkNanos(1000 * 1000 * 1000 * 2L);
        myService.testAgent();
        System.out.println("MyController executed.");
        return "Test Agent Success.";
    }
}