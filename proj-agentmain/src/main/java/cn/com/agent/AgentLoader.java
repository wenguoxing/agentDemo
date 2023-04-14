package cn.com.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/13 17:18
 * @Version 1.0
 */
public class AgentLoader {
    public static void main(String[] args) throws Exception {

        //被attach的实例运行时，classpath需要引入org.apache.bcel包

        if (args.length < 2) {
            System.err.println("Usage: java -cp .:$JAVA_HOME/lib/tools.jar"
                    + " cn.com.agent.AgentLoader <pid/name> <agent> [options]");
            System.exit(0);
        }

        String jvmPid = args[0];
        String agentJar = args[1];
        String options = args.length > 2 ? args[2] : null;
        for (VirtualMachineDescriptor jvm : VirtualMachine.list()) {
            //if (jvm.displayName().contains(args[0])) {
            if (jvm.id().equals(args[0])) {
                jvmPid = jvm.id();
                break;
            }
        }

        VirtualMachine jvm = VirtualMachine.attach(jvmPid);
        jvm.loadAgent(agentJar, options);
        jvm.detach();
    }
}