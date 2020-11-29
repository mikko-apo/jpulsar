package jpulsar.tester;

import jpulsar.scan.TestClass;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.TestMethod;
import jpulsar.step.TestStepCollector;
import jpulsar.util.Benchmark;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static jpulsar.util.Collections.filter;
import static jpulsar.util.Collections.map;

// TODO: Capture System.out, System.err
// TODO: Release System.out, System.err
public class SerialTester {
    public static TestRunResult runTests(TestScanResult scanInfo) {

        List<TestClass<?>> testClasses = filter(scanInfo.getTestClasses(), testClass -> testClass.getTestMethods().size() > 0);
        try (TestResourceManager manager = new TestResourceManager()) {
            return new TestRunResult(map(testClasses,
                    testClass -> {
                        TestClassResult<?> testClassResult = new TestClassResult<>(testClass.getClazz(),
                                map(testClass.getTestMethods(),
                                        testMethod -> runTestMethod(manager, testClass, testMethod)));
                        manager.cleanClassResourceHandlers();
                        return testClassResult;
                    }
            ));
        }
    }

    private static <T> TestMethodResult runTestMethod(TestResourceManager manager,
                                                      TestClass<T> testClass,
                                                      TestMethod testMethod) {
        final AtomicReference<Throwable> exception = new AtomicReference<>(null);
        Benchmark benchmark = new Benchmark();
        TestStepCollector testStepCollector = new TestStepCollector(benchmark.start);

        ConstructorInfo constructorInfo = testClass.getConstructor();
        Constructor<?> testClassConstructor = constructorInfo.getConstructor();
        try {
            // TODO: Needs to resolve TestClass constructor @TestResource parameters recursively
            Object testClassInstance = testClassConstructor.newInstance();
            manager.withTestResourceParameters(testMethod.getParameterTestResources(), (objects) ->
                    {
                        try {
                            testMethod.getMethod().invoke(testClassInstance, objects);
                        } catch (InvocationTargetException e) {
                            exception.set(e.getCause());
                        } catch (Exception e) {
                            exception.set(e);
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
            exception.set(e);
        }

        ExceptionResult exceptionResult = null;
        if (exception.get() != null) {
            exceptionResult = new ExceptionResult(exception.get());
        }
        return new TestMethodResult(testMethod.getMethod().getName(),
                exceptionResult,
                benchmark.durationMsAndSet(),
                testStepCollector.getSteps());
    }
}
