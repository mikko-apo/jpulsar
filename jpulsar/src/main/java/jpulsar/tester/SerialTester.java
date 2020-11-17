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

import static jpulsar.util.Streams.map;

public class SerialTester {
    public static TestRunResult runTests(TestScanResult scanInfo) {

        return new TestRunResult(map(scanInfo.getTestClasses(),
                testClass -> new TestClassResult<>(testClass.getClazz(),
                        map(testClass.getTestMethods(),
                                testMethod -> runTestMethod(testClass, testMethod)))
        ));
    }

    private static <T> TestMethodResult runTestMethod(TestClass<T> testClass, TestMethod testMethod) {
        try {
            ConstructorInfo constructorInfo = testClass.getConstructorInfo();
            Class<?>[] constructorParameters = constructorInfo.getMethodParameterTypes();
            Constructor<T> testClassConstructor = testClass
                    .getClazz()
                    .getConstructor(constructorParameters);
            Object testClassInstance = testClassConstructor.newInstance();
            Class<?>[] parameterTypes = testMethod.getMethodParameterTypes();
            Method method = testClass.getClazz().getMethod(testMethod.getMethodName(), parameterTypes);
            Throwable exception = null;
            long durationMs;
            Benchmark benchmark = new Benchmark();
            TestStepCollector testStepCollector = new TestStepCollector(benchmark.start);
            try {
                method.invoke(testClassInstance);
            } catch (InvocationTargetException e) {
                exception = e.getCause();
            } catch (Exception e) {
                exception = e;
            } finally {
                durationMs = benchmark.durationMsAndSet();
            }
            ExceptionResult exceptionResult = null;
            if (exception != null) {
                exceptionResult = new ExceptionResult(exception);
            }
            return new TestMethodResult(testMethod.getMethodName(), exceptionResult, durationMs, testStepCollector.getSteps());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
