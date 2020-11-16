package jpulsar.scan.method;

import jpulsar.scan.Issues;

import java.util.List;


public class TestMethodBase extends Issues {
    private String methodName;
    private int modifiers;
    private List<Class<?>> methodParameterTypes;

    public TestMethodBase(String methodName, int modifiers, List<Class<?>> methodParameterTypes) {
        this.methodName = methodName;
        this.modifiers = modifiers;
        this.methodParameterTypes = methodParameterTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Class<?>> getMethodParameterTypes() {
        return methodParameterTypes;
    }

    public int getModifiers() {
        return modifiers;
    }
}
