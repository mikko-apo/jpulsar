package jpulsar.scan.method;

import jpulsar.scan.Issues;


public class TestMethodBase extends Issues {
    private int modifiers;
    private String methodName;
    private MethodParameterInfo methodParameters;
    private final TypeSignature returnType;

    public TestMethodBase(String methodName, int modifiers, MethodParameterInfo methodParameters, TypeSignature returnType) {
        this.methodName = methodName;
        this.modifiers = modifiers;
        this.methodParameters = methodParameters;
        this.returnType = returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public MethodParameterInfo getMethodParameters() {
        return methodParameters;
    }

    public int getModifiers() {
        return modifiers;
    }

    public TypeSignature getReturnType() {
        return returnType;
    }
}
