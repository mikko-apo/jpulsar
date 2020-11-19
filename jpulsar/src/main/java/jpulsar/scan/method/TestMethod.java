package jpulsar.scan.method;

import jpulsar.scan.annotationdata.TestAnnotationData;

public class TestMethod extends TestMethodBase {
    private TestAnnotationData testAnnotationData;

    public TestMethod(String methodName,
                      int modifiers,
                      MethodParameterInfo methodParameters,
                      TestAnnotationData testAnnotation) {
        super(methodName, modifiers, methodParameters, null);
        testAnnotationData = testAnnotation;
    }

    public TestAnnotationData getTestAnnotationData() {
        return testAnnotationData;
    }
}
