package cn.com.agent.starter;

import com.sun.tools.attach.VirtualMachine;
import org.springframework.core.env.ConfigurableEnvironment;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/14 13:01
 * @Version 1.0
 */
public class MyAgentLoader {
    private static final int TEMP_DIR_ATTEMPTS = 10000;

    private static final String AGENT_ZIP_NAME = "myagent-core-jar-with-dependencies.zip";
    private static final String AGENT_JAR_NAME = "myagent-core-jar-with-dependencies.jar";

    private final ConfigurableEnvironment environment;

    public MyAgentLoader(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    /**
     * 创建用于存放Java Agent的jar包的临时目录；
     * 从classpath下找到Java Agent的zip包；
     * 将Java Agent的zip包解压到刚创建出来的临时目录中；
     * 拿到主进程Id；
     * 从Environment中拿到配置的目标包路径；
     * 基于VirtualMachine附加到主进程上；
     * 加载Java Agent，并传入目标包路径。
     *
     * @throws IOException IOException
     */
    public void load() throws IOException {
        // 创建临时目录用于存放agent的jar包
        File tempDir = createTempDir();
        // 解压得到agent的jar包并放到临时目录
        URL agentJarUrl = this.getClass().getClassLoader().getResource(AGENT_ZIP_NAME);
        ZipUtil.unpack(agentJarUrl.openStream(), tempDir);

        // 拿到主进程Id
        String jvmId = getJvmId();
        String basePackage = environment.getProperty("myagent.basepackage");
        try {
            // Attach到主进程
            VirtualMachine virtualMachine = VirtualMachine.attach(jvmId);
            // 加载Java Agent，并传入包路径
            virtualMachine.loadAgent(new File(tempDir.getAbsolutePath(), AGENT_JAR_NAME).getAbsolutePath(), basePackage);
            virtualMachine.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File createTempDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = "myagent-" + System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }

    private static String getJvmId() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String runtimeMXBeanName = runtimeMXBean.getName();
        return runtimeMXBeanName.split("@")[0];
    }
}