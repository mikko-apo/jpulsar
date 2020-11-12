package jpulsar.scan;

import jpulsar.scan.annotationdata.TestResourceAnnotationData;

import java.util.List;

class TestResourceMethod extends TestMethodBase {
    private final TestResourceAnnotationData testResourceAnnotation;

    public TestResourceMethod(
            String methodName,
            List<Class<?>> parameters,
            TestResourceAnnotationData testResourceAnnotation) {
        super(methodName, parameters);
        this.testResourceAnnotation = testResourceAnnotation;
    }

    public TestResourceAnnotationData getTestResourceAnnotation() {
        return testResourceAnnotation;
    }
}
