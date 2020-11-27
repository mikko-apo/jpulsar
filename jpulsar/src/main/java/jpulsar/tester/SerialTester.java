package jpulsar.tester;

import jpulsar.scan.TestClass;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.step.TestStepCollector;
import jpulsar.util.Benchmark;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static jpulsar.util.Collections.map;

// TODO: Capture System.out, System.err
// TODO: Release System.out, System.err
public class SerialTester {
    public static TestRunResult runTests(TestScanResult scanInfo) {

        return new TestRunResult(map(scanInfo.getTestClasses(),
                testClass -> new TestClassResult<>(testClass.getClazz(),
                        map(testClass.getTestMethods(),
                                testMethod -> runTestMethod(testClass, testMethod)))
        ));
        // TODO: .afterAll() lifecycle methods
        // TODO: .afterAll() lifecycle methods called when @TestResource is released
    }

    private static <T> TestMethodResult runTestMethod(TestClass<T> testClass, TestMethod testMethod) {
        Throwable exception = null;
        Benchmark benchmark = new Benchmark();
        TestStepCollector testStepCollector = new TestStepCollector(benchmark.start);

        ConstructorInfo constructorInfo = testClass.getConstructor();
        Constructor<?> testClassConstructor = constructorInfo.getConstructor();
        try {
            // TODO: Needs to resolve TestClass constructor @TestResource parameters recursively
            // TODO: .beforeAll() lifecycle method called when created
            Object testClassInstance = testClassConstructor.newInstance();
            Object[] params = resolveParametersFromTestResourceMethods(testMethod.getParameterTestResources());
            try {
                // TODO: .before() lifecycle methods
                testMethod.getMethod().invoke(testClassInstance, params);
            } catch (InvocationTargetException e) {
                exception = e.getCause();
            } finally {
                // TODO: .after() lifecycle methods
            }
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }

        ExceptionResult exceptionResult = null;
        if (exception != null) {
            exceptionResult = new ExceptionResult(exception);
        }
        return new TestMethodResult(testMethod.getMethod().getName(), exceptionResult, benchmark.durationMsAndSet(), testStepCollector.getSteps());
    }

    private static Object[] resolveParametersFromTestResourceMethods(List<TestResourceMethod> testResources) {
        Object[] params = map(testResources, testResourceMethod -> {
            try {
                return initializeAndCallTestResourceMethod(testResourceMethod);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }).toArray();
        return params;
    }

    private static Object initializeAndCallTestResourceMethod(TestResourceMethod testResourceMethod) throws
            InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Method m = testResourceMethod.getMethod();
        // TODO: Needs to resolve @TestResource's @TestResource parameters recursively
        if (Modifier.isStatic(m.getModifiers())) {
            return m.invoke(null);
        } else {
            // TODO: Needs to resolve @TestResource's getDeclaringClass' constructor @TestResource parameters recursively
            Constructor<?> constructor = m.getDeclaringClass().getConstructor();
            Object instance = constructor.newInstance();
            return m.invoke(instance);
        }
    }
}
