package com.lee.learn.agent.test.outter;

import java.util.concurrent.locks.LockSupport;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/14 11:30
 * @Version 1.0
 */
public class OuterTask {
    public void execute() {
        System.out.println("Start to execute outer task.");
        LockSupport.parkNanos(1000 * 1000 * 1000 * 2L);
    }

    public void close() {
        System.out.println("Start to close outer task.");
        LockSupport.parkNanos(1000 * 1000 * 1000 * 3L);
    }
}