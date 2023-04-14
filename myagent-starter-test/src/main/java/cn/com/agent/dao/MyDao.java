package cn.com.agent.dao;

import org.springframework.stereotype.Repository;

import java.util.concurrent.locks.LockSupport;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/14 13:09
 * @Version 1.0
 */

@Repository
public class MyDao {
    public void testAgent() {
        LockSupport.parkNanos(1000 * 1000 * 1000 * 2L);
        System.out.println("MyDao executed.");
    }
}