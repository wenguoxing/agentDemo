import com.lee.learn.agent.test.inner.InnerTask;
import com.lee.learn.agent.test.outter.OuterTask;
import com.sun.tools.attach.VirtualMachine;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/14 11:31
 * @Version 1.0
 */
public class MainTest {
    private static final String agentPath = "F:\\Ebook\\java-agent\\agentDemo\\myagent-core\\target\\myagent-core-jar-with-dependencies.jar";

    public static void main(String[] args) {
        loadAgent();

        InnerTask innerTask = new InnerTask();
        innerTask.execute();
        innerTask.close();

        OuterTask outerTask = new OuterTask();
        outerTask.execute();
        outerTask.close();
    }

    private static void loadAgent() {
        try {
            // 获取主进程Id
            String jvmId = getJvmId();
            // Attach到主进程
            VirtualMachine virtualMachine = VirtualMachine.attach(jvmId);
            // 加载Java Agent，并指定包路径
            //virtualMachine.loadAgent(agentPath, "com.lee.learn.agent.test.inner");
            virtualMachine.loadAgent(agentPath, "com.lee.learn.agent.test.inner,com.lee.learn.agent.test.outter");
            virtualMachine.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getJvmId() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String runtimeMXBeanName = runtimeMXBean.getName();
        return runtimeMXBeanName.split("@")[0];
    }
}