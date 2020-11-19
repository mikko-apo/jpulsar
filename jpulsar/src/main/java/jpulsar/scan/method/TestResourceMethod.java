package jpulsar.scan.method;

import jpulsar.scan.annotationdata.TestResourceAnnotationData;

public class TestResourceMethod extends TestMethodBase {
    private final TestResourceAnnotationData testResourceAnnotation;

    public TestResourceMethod(
            String methodName,
            int modifiers,
            MethodParameterInfo methodParameters,
            TypeSignature returnType,
            TestResourceAnnotationData testResourceAnnotation) {
        super(methodName, modifiers, methodParameters, returnType);
        this.testResourceAnnotation = testResourceAnnotation;
    }

    public TestResourceAnnotationData getTestResourceAnnotation() {
        return testResourceAnnotation;
    }
}

