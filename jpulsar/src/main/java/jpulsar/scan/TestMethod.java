package jpulsar.scan;

import jpulsar.scan.annotationdata.TestAnnotationData;

import java.util.List;

public class TestMethod extends TestMethodBase {
    private TestAnnotationData testAnnotationData;

    public TestMethod(String methodName, List<Class<?>> methodParameterTypes, TestAnnotationData testAnnotation) {
        super(methodName, methodParameterTypes);
        testAnnotationData = testAnnotation;
    }

    public TestAnnotationData getTestAnnotationData() {
        return testAnnotationData;
    }
}
