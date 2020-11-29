package jpulsar.scan.method;

import jpulsar.ResourceHandler;
import jpulsar.TestResource;
import jpulsar.TestResourceScope;
import jpulsar.scan.Issues;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    public boolean persistent() {
        return testResourceAnnotation.shared() || testResourceAnnotation.max() > 0;
    }

    public String name() {
        String name = testResourceAnnotation.name();
        return name.equals("") ? null : name;
    }

    public Type actualReturnType() {
        Type returnType = getMethod().getGenericReturnType();
        if(returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if(rawType.equals(ResourceHandler.class) ) {
                return parameterizedType.getActualTypeArguments()[0];
            }
        }
        return returnType;
    }
}

