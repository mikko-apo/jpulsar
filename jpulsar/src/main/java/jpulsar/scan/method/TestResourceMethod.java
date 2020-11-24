package jpulsar.scan.method;

import jpulsar.TestResource;
import jpulsar.TestResourceScope;
import jpulsar.scan.Issues;

import java.lang.reflect.Method;

public class TestResourceMethod extends Issues {
    public static int MaxDefault = 0;

    private final Method method;
    private final TestResource testResourceAnnotation;
    private final boolean classHasTests;

    public TestResourceMethod(Method method, TestResource testResourceAnnotation, boolean classHasTests) {

        this.method = method;
        this.testResourceAnnotation = testResourceAnnotation;
        this.classHasTests = classHasTests;
    }

    public Method getMethod() {
        return method;
    }

    public TestResource getTestResourceAnnotation() {
        return testResourceAnnotation;
    }

    public boolean isClassHasTests() {
        return classHasTests;
    }

    public TestResourceScope scope() {
        TestResourceScope scope = testResourceAnnotation.scope();
        if (scope == TestResourceScope.DEFAULT) {
            scope = classHasTests ? TestResourceScope.CLASS : TestResourceScope.GLOBAL;
        }
        return scope;
    }
}

