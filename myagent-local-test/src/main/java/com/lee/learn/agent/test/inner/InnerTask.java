package com.lee.learn.agent.test.inner;

import java.util.concurrent.locks.LockSupport;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/14 11:30
 * @Version 1.0
 */
public class InnerTask {
    public void execute() {
        System.out.println("Begin to execute inner task.");
        LockSupport.parkNanos(1000 * 1000 * 1000 * 2L);
    }

    public void close() {
        System.out.println("Begin to close inner task.");
        LockSupport.parkNanos(1000 * 1000 * 1000 * 3L);
    }
}