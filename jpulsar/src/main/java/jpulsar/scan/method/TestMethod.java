package jpulsar.scan.method;

import jpulsar.Test;
import jpulsar.scan.Issues;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestMethod extends Issues {

    private final Method method;
    private final Test testAnnotation;
    private final List<TestResourceMethod> parameterTestResources = new ArrayList<>();

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

    public TestMethod addParameterTestResource(TestResourceMethod testResourceMethod) {
        parameterTestResources.add(testResourceMethod);
        return this;
    }

    public List<TestResourceMethod> getParameterTestResources() {
        return parameterTestResources;
    }
}
