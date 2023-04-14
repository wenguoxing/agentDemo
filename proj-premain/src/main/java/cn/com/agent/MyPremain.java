package cn.com.agent;

import java.lang.instrument.Instrumentation;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/13 16:33
 * @Version 1.0
 */
public class MyPremain {
    public static void premain(String agentArgs, Instrumentation inst) {
        // 1.在main方法执行前追加一行。
        System.out.println(agentArgs);

        // 2.修改getGreeting方法的内容
        inst.addTransformer(new GreetingTransformer(agentArgs));
    }
}