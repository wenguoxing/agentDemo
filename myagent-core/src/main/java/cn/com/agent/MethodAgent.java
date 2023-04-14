package cn.com.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 主体类
 *
 * @Author: wenguoxing
 * @Date: 2023/4/14 10:00
 * @Version 1.0
 */
public class MethodAgent {
    public static final String REGEX_QUALIFIER = "\\.";
    private static final String REGEX_CLASS_SUFFIX = "\\.class";

    private static final String QUALIFIER = ".";
    private static final String CLASS_SUFFIX = ".class";
    public static final String PATH_SEP = "/";

    private static final String SEP = ",";
    private static final String EMPTY = "";

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        if (agentArgs == null) {
            return;
        }
        // 期望agentArgs传入的是一个以英文逗号分隔的多个路径
        String[] basePackages = agentArgs.split(SEP);
        List<Class<?>> targetClasses = new ArrayList<>();
        for (String basePackage : basePackages) {
            try {
                // 获取到传入路径下的所有类的类对象
                findClasses(basePackage, targetClasses, MethodAgent.class.getClassLoader());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        // 基于获取到的类的类对象集合创建MTransformer，并向Instrumentation注册MTransformer转换器
        // 需要指定canRetransform为true，否则下面调用的retransformClasses()方法会不生效
        instrumentation.addTransformer(new MTransformer(targetClasses), true);
        try {
            // 将所有目标类通过retransformClasses()方法传递到MTransformer转换器完成转换
            instrumentation.retransformClasses(targetClasses.toArray(new Class<?>[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * findClasses
     *
     * @param basePackage basePackage
     * @param clazzList   clazzList
     * @param classLoader classLoader
     * @throws IOException            IOException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private static void findClasses(String basePackage, List<Class<?>> clazzList, ClassLoader classLoader)
            throws IOException, ClassNotFoundException {
        // 获取传入路径下的所有类的类对象
        Enumeration<URL> resources = classLoader.getResources(basePackage.replaceAll(REGEX_QUALIFIER, PATH_SEP));
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String[] fileNames = new File(url.getPath()).list();
            if (fileNames == null || fileNames.length == 0) {
                return;
            }
            for (String fileName : fileNames) {
                if (!fileName.endsWith(CLASS_SUFFIX)) {
                    findClasses(basePackage + QUALIFIER + fileName, clazzList, classLoader);
                } else {
                    clazzList.add(Class.forName(basePackage + QUALIFIER + fileName.replaceAll(REGEX_CLASS_SUFFIX, EMPTY)));
                }
            }
        }
    }
}