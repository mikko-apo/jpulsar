package jpulsar.scan.method;

import jpulsar.scan.annotationdata.TestAnnotationData;

public class TestMethod extends TestMethodBase {
    private TestAnnotationData testAnnotationData;

    public TestMethod(String methodName, int modifiers, Class<?>[] methodParameterTypes, TestAnnotationData testAnnotation) {
        super(methodName, modifiers, methodParameterTypes);
        testAnnotationData = testAnnotation;
    }

    public TestAnnotationData getTestAnnotationData() {
        return testAnnotationData;
    }
}
