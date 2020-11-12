package jpulsar.scan;

import java.util.List;

public class TestMethodBase extends Issues {
    private String methodName;
    private List<Class<?>> methodParameterTypes;

    public TestMethodBase(String methodName, List<Class<?>> methodParameterTypes) {
        this.methodName = methodName;
        this.methodParameterTypes = methodParameterTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Class<?>> getMethodParameterTypes() {
        return methodParameterTypes;
    }
}
