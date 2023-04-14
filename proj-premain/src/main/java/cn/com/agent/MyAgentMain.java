package cn.com.agent;

import java.lang.instrument.Instrumentation;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/13 17:29
 * @Version 1.0
 */
public class MyAgentMain {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new GreetingTransformer(agentArgs), true);
        try {
            Class clazz = Class.forName("cn.com.agent.App");
            if (inst.isModifiableClass(clazz)) {
                inst.retransformClasses(clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}