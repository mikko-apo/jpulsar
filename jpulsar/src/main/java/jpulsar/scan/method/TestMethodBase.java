package jpulsar.scan.method;

import jpulsar.scan.Issues;

import java.util.List;


public class TestMethodBase extends Issues {
    private String methodName;
    private int modifiers;
    private Class<?>[] methodParameterTypes;

    public TestMethodBase(String methodName, int modifiers, Class<?>[] methodParameterTypes) {
        this.methodName = methodName;
        this.modifiers = modifiers;
        this.methodParameterTypes = methodParameterTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getMethodParameterTypes() {
        return methodParameterTypes;
    }

    public int getModifiers() {
        return modifiers;
    }
}
