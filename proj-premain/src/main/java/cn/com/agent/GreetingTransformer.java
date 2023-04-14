package cn.com.agent;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/13 16:53
 * @Version 1.0
 */
public class GreetingTransformer implements ClassFileTransformer {
    private String agentArgs;

    public GreetingTransformer(String agentArgs) {
        this.agentArgs = agentArgs;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!className.equals("cn/com/agent/App")) {
            return classfileBuffer;
        }
        try {
            JavaClass clazz = Repository.lookupClass(className);
            ClassGen cg = new ClassGen(clazz);
            ConstantPoolGen cp = cg.getConstantPool();
            for (Method method : clazz.getMethods()) {
                // 修改 App中的getGreeting方法内容
                if (method.getName().equals("getGreeting")) {
                    MethodGen mg = new MethodGen(method, cg.getClassName(), cp);
                    InstructionList il = new InstructionList();
                    // 修改内容
                    il.append(new PUSH(cp, this.agentArgs));
                    il.append(InstructionFactory.createReturn(Type.STRING));
                    mg.setInstructionList(il);
                    mg.setMaxStack();
                    mg.setMaxLocals();
                    cg.replaceMethod(method, mg.getMethod());
                }
            }
            return cg.getJavaClass().getBytes();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}