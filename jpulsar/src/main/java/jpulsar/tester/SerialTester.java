package jpulsar.tester;

import jpulsar.scan.TestClass;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.TestMethod;
import jpulsar.step.TestStepCollector;
import jpulsar.util.Benchmark;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static jpulsar.util.Collections.map;

public class SerialTester {
    public static TestRunResult runTests(TestScanResult scanInfo) {

        return new TestRunResult(map(scanInfo.getTestClasses(),
                testClass -> new TestClassResult<>(testClass.getClazz(),
                        map(testClass.getTestMethods(),
                                testMethod -> runTestMethod(testClass, testMethod)))
        ));
    }

    private static <T> TestMethodResult runTestMethod(TestClass<T> testClass, TestMethod testMethod) {
        Throwable exception = null;
        Benchmark benchmark = new Benchmark();
        TestStepCollector testStepCollector = new TestStepCollector(benchmark.start);

        ConstructorInfo constructorInfo = testClass.getConstructor();
        Class<?>[] constructorParameters = constructorInfo.getConstructor().getParameterTypes();
        try {
            Constructor<T> testClassConstructor = testClass
                    .getClazz()
                    .getConstructor(constructorParameters);
            Object testClassInstance = testClassConstructor.newInstance();
            Class<?>[] parameterTypes = testMethod.getMethod().getParameterTypes();
            Method method = testClass.getClazz().getMethod(testMethod.getMethod().getName(), parameterTypes);
            try {
                method.invoke(testClassInstance);
            } catch (InvocationTargetException e) {
                exception = e.getCause();
            }
        } catch ( Exception e) {
            exception = e;
        }

        ExceptionResult exceptionResult = null;
        if (exception != null) {
            exceptionResult = new ExceptionResult(exception);
        }
        return new TestMethodResult(testMethod.getMethod().getName(), exceptionResult, benchmark.durationMsAndSet(), testStepCollector.getSteps());
    }
}
