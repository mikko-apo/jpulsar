package jpulsar.scan.method;

import jpulsar.Test;
import jpulsar.scan.Issues;

import java.lang.reflect.Method;

public class TestMethod extends Issues {

    private final Method method;
    private final Test testAnnotation;

    public TestMethod(Method method, Test testAnnotation) {
        this.method = method;
        this.testAnnotation = testAnnotation;
    }

    public Method getMethod() {
        return method;
    }

    public Test getTestAnnotation() {
        return testAnnotation;
    }
}
