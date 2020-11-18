package jpulsar.scan.method;

import jpulsar.scan.annotationdata.TestResourceAnnotationData;

public class TestResourceMethod extends TestMethodBase {
    private final MethodReturnType returnType;
    private final TestResourceAnnotationData testResourceAnnotation;

    public TestResourceMethod(
            String methodName,
            int modifiers,
            Class<?>[] parameters,
            MethodReturnType returnType,
            TestResourceAnnotationData testResourceAnnotation) {
        super(methodName, modifiers, parameters);
        this.returnType = returnType;
        this.testResourceAnnotation = testResourceAnnotation;
    }

    public TestResourceAnnotationData getTestResourceAnnotation() {
        return testResourceAnnotation;
    }

    public MethodReturnType getReturnType() {
        return returnType;
    }
}

