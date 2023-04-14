package cn.com.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义转换器
 *
 * @Author: wenguoxing
 * @Date: 2023/4/14 10:01
 * @Version 1.0
 */
public class MTransformer implements ClassFileTransformer {

    private static final String QUALIFIER = ".";
    public static final String PATH_SEP = "/";
    public static final String REGEX_QUALIFIER = "\\.";

    /**
     * 目标类的类全限定名和类加载器的映射，用于筛选出需要重定义的类。
     * 映射类型是：Map[类全限定名, 类加载器]。
     */
    private final Map<String, ClassLoader> targetClassesMap = new HashMap<>();

    /**
     * 初始化时就需要传入目标类集合，并转换成映射关系。     *
     * 需要传入目标类的类对象的集合，目的就是做到动态的控制对哪些类添加方法耗时统计的逻辑
     *
     * @param targetClasses 目标类的类对象集合。
     */
    public MTransformer(List<Class<?>> targetClasses) {
        targetClasses.forEach(targetClass ->
                targetClassesMap.put(
                        targetClass.getName(),
                        targetClass.getClassLoader()));
    }

    /**
     * 基于Javassist改造类方法，为每个方法添加打印执行耗时的逻辑。
     *
     * @param loader 需要改造的类的类加载器。
     * @param className 需要改造的类的类全限定名。
     * @param classBeingRedefined 有值时传入的就是正在被重定义的类的类对象，如果是类加载阶段那么传入为null。
     * @param protectionDomain 改造类的保护域。
     * @param classfileBuffer 类文件的输入字节缓冲区。
     * @return 改造后的类文件字节缓存区，如果未执行改造，返回null。
     */
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        byte[] byteCode = classfileBuffer;
        // 类加载器+类路径才能完全确定一个类
        // 基于类加载器和类路径进行目标类筛选
        String targetClassName = className.replaceAll(PATH_SEP, REGEX_QUALIFIER);
        if (targetClassesMap.get(targetClassName) == null
                || !targetClassesMap.get(targetClassName).equals(loader)) {
            return byteCode;
        }
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(targetClassName);
            // 对目标类的所有方法都插入统计耗时逻辑
            CtMethod[] declaredMethods = ctClass.getDeclaredMethods();
            // 遍历每个方法
            for (CtMethod ctMethod : declaredMethods) {
                // 插入统计耗时逻辑
                transformMethod(ctMethod);
            }
            byteCode = ctClass.toBytecode();
            ctClass.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteCode;
    }

    /**
     * 为每个方法添加统计执行耗时的逻辑。
     *
     * @param ctMethod 详情见{@link CtMethod}。
     */
    private void transformMethod(CtMethod ctMethod) throws Exception {
        // 在方法内添加本地参数
        ctMethod.addLocalVariable("beginTime", CtClass.longType);
        ctMethod.addLocalVariable("endTime", CtClass.longType);
        ctMethod.addLocalVariable("executeTime", CtClass.longType);

        // 方法体之前添加统计开始时间的代码
        ctMethod.insertBefore("beginTime = System.currentTimeMillis();");

        // 方法体结束位置添加获取结束时间并计算执行耗时的代码
        String endCode = "endTime = System.currentTimeMillis();" +
                "executeTime = endTime - beginTime;" +
                "System.out.println(executeTime);";
        ctMethod.insertAfter(endCode);
    }

}