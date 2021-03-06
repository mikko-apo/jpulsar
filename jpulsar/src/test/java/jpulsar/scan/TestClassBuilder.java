package jpulsar.scan;

import jpulsar.TestResource;
import jpulsar.TestResourceScope;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static jpulsar.util.Util.scannerJackson;

public class TestClassBuilder {
    public TestClass<?> testClass;
    public final List<TestClass<?>> testClasses = new ArrayList<>();

    static public jpulsar.Test createTestAnnotation(String name, String[] usecases, String[] tags) {
        return new jpulsar.Test() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return jpulsar.Test.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String[] usecases() {
                return usecases;
            }

            @Override
            public String[] tags() {
                return tags;
            }
        };
    }

    public static TestResource createTestResourceAnnotation(String name,
                                                            Integer max,
                                                            Boolean shared,
                                                            Boolean fixed,
                                                            Boolean hidden,
                                                            TestResourceScope scope,
                                                            String[] usecases) {
        return new jpulsar.TestResource() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return jpulsar.TestResource.class;
            }

            @Override
            public int max() {
                return max;
            }

            @Override
            public boolean shared() {
                return shared;
            }

            @Override
            public boolean fixed() {
                return fixed;
            }

            @Override
            public boolean hidden() {
                return hidden;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public TestResourceScope scope() {
                return scope;
            }

            @Override
            public String[] usecases() {
                return usecases;
            }
        };
    }

    public <T> TestClass<T> addTestClass(Class<T> aClass) {
        TestClass<T> testClass = new TestClass<>(aClass);
        testClass.setConstructor(new ConstructorInfo(aClass.getDeclaredConstructors()[0]));
        this.testClass = testClass;
        testClasses.add(testClass);
        return testClass;
    }

    public TestMethod addTestMethod(jpulsar.Test testAnnotation, String name, Class<?>... parameterTypes) {
        TestMethod testMethod = new TestMethod(
                getMethod(testClass.getClazz(), name, parameterTypes),
                testAnnotation);
        testClass.addTestMethod(testMethod);
        return testMethod;
    }

    public TestResourceMethod addTestResourceMethod(TestResource testResourceAnnotation, String name, boolean classHasTests, Class<?>... parameterTypes) {
        TestResourceMethod testResource = new TestResourceMethod(
                getMethod(testClass.getClazz(), name, parameterTypes),
                testResourceAnnotation,
                classHasTests);
        testClass.addTestResource(testResource);
        return testResource;
    }

    public void compareTestClasses(List<TestClass<?>> testClasses) {
        sortTestClasses(testClasses);
        sortTestClasses(this.testClasses);
        scannerJackson.jsonEquals(this.testClasses, testClasses);
    }

    private static void sortTestClasses(List<TestClass<?>> testClasses) {
        Comparator<TestClass<?>> testClassComparator = Comparator.comparing(testClass -> testClass.getClazz().getName());
        Comparator<TestMethod> testMethodComparator = Comparator.comparing(
                testMethod -> testMethod.getMethod().getName());
        Comparator<TestResourceMethod> testResourceMethodComparator = Comparator.comparing(
                testResourceMethod -> testResourceMethod.getMethod().getName());
        Comparator<String> stringComparator = Comparator.comparing(s -> s);
        testClasses.sort(testClassComparator);
        for (TestClass<?> testClass : testClasses) {
            testClass.getTestMethods().sort(testMethodComparator);
            for(TestMethod testMethod : testClass.getTestMethods()) {
                testMethod.getIssues().sort(stringComparator);
            }
            testClass.getTestResources().sort(testResourceMethodComparator);
            for(TestResourceMethod testResourceMethod : testClass.getTestResources()) {
                testResourceMethod.getIssues().sort(stringComparator);
            }
            testClass.getIssues().sort(stringComparator);
        }
    }

    static private Method getMethod(Class<?> aClass, String name, Class<?>... parameterTypes) {
        try {
            return aClass.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                return aClass.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException noSuchMethodException) {
                throw new RuntimeException(noSuchMethodException);
            }
        }
    }

}
