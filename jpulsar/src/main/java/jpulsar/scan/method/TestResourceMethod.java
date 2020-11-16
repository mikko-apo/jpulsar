package jpulsar.scan.method;

import jpulsar.scan.annotationdata.TestResourceAnnotationData;

import java.util.List;

public class TestResourceMethod extends TestMethodBase {
    private final TestResourceAnnotationData testResourceAnnotation;

    public TestResourceMethod(
            String methodName,
            int modifiers, List<Class<?>> parameters,
            TestResourceAnnotationData testResourceAnnotation) {
        super(methodName, modifiers, parameters);
        this.testResourceAnnotation = testResourceAnnotation;
    }

    public TestResourceAnnotationData getTestResourceAnnotation() {
        return testResourceAnnotation;
    }
}
