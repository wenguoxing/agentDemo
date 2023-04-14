package cn.com.agent.service;

import cn.com.agent.dao.MyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.LockSupport;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/14 13:30
 * @Version 1.0
 */

@Service
public class MyService {
    @Autowired
    private MyDao myDao;

    public void testAgent() {
        LockSupport.parkNanos(1000 * 1000 * 1000 * 2L);
        myDao.testAgent();
        System.out.println("MyService executed.");
    }
}