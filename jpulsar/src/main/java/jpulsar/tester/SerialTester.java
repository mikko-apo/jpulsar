package jpulsar.tester;

import jpulsar.ResourceHandler;
import jpulsar.lifecycle.LifeCycleOperation;
import jpulsar.lifecycle.TestLifecycleOperations;
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
import java.util.function.Consumer;
import java.util.function.Function;

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
            // TODO: needs to support @Test annotations shared, max
            Object testClassInstance = testClassConstructor.newInstance();
            MethodParameters methodParameters = resolveParametersFromTestResourceMethods(testMethod.getParameterTestResources());
            executeLifecycleOps(methodParameters.getResourceHandlers(), TestLifecycleOperations::getBeforeAlls);
            executeLifecycleOps(methodParameters.getResourceHandlers(), TestLifecycleOperations::getBefores);
            try {
                testMethod.getMethod().invoke(testClassInstance, methodParameters.parameterArray());
            } catch (InvocationTargetException e) {
                exception = e.getCause();
            } finally {
                executeLifecycleOps(methodParameters.getResourceHandlers(), TestLifecycleOperations::getAfters);
                executeLifecycleOps(methodParameters.getResourceHandlers(), TestLifecycleOperations::getAfterAlls);
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

    private static void executeLifecycleOps(List<ResourceHandler<?>> resourceHandlers,
                                            Function<ResourceHandler<?>, List<LifeCycleOperation<?>>> opListSupplier) {
        for (ResourceHandler<?> resourceHandler : resourceHandlers) {
            Object resource = resourceHandler.getResource();
            for (LifeCycleOperation<?> op : opListSupplier.apply(resourceHandler)) {
                executeLifecycleOp(resource, op);
            }
        }
    }

    private static <T> void executeLifecycleOp(Object resource, LifeCycleOperation<T> op) {
        Consumer<T> handler = op.getHandler();
        if (handler != null) {
            handler.accept((T)resource);
        }
        if(op.getRunner() != null) {
            op.getRunner().run();
        }
    }

    private static MethodParameters resolveParametersFromTestResourceMethods(List<TestResourceMethod> testResources) {
        MethodParameters methodParameters = new MethodParameters();
        for (TestResourceMethod testResourceMethod : testResources) {
            try {
                Object param = initializeAndCallTestResourceMethod(testResourceMethod);
                if (param instanceof ResourceHandler) {
                    ResourceHandler<?> resourceHandler = (ResourceHandler<?>) param;
                    methodParameters.addResourceHandler(resourceHandler);
                    param = resourceHandler.getResource();
                }
                methodParameters.addParameter(param);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return methodParameters;
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
