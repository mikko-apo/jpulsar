package jpulsar.tester;

import jpulsar.scan.TestClass;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.TestMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static jpulsar.util.Streams.map;

public class SerialTester {
    public static TestRunResult runTests(TestScanResult scanInfo) {

        return new TestRunResult(map(scanInfo.getTestClasses(),
                testClass -> new TestClassResult(testClass.getClazz(),
                        map(testClass.getTestMethods(),
                                testMethod -> runTestMethod(testClass, testMethod)))
        ));
    }

    private static TestMethodResult runTestMethod(TestClass<?> testClass, TestMethod testMethod) {
        try {
            ConstructorInfo constructorInfo = testClass.getConstructorInfo();
            Class<?>[] constructorParameters = (Class<?>[]) constructorInfo.getMethodParameterTypes().toArray();
            Constructor<Class<?>> testClassConstructor = (Constructor<Class<?>>) testClass
                    .getClazz()
                    .getConstructor(constructorParameters);
            Object testClassInstance = testClassConstructor.newInstance();
            Class<?>[] parameterTypes = (Class<?>[]) testMethod.getMethodParameterTypes().toArray();
            Method method = testClass.getClazz().getMethod(testMethod.getMethodName(), parameterTypes);
            Throwable exception = null;
            try {
                method.invoke(testClassInstance);
            } catch (InvocationTargetException e) {
                exception = e.getCause();
            } catch (Exception e) {
                exception = e;
            }
            ExceptionResult exceptionResult = null;
            if (exception != null) {
                exceptionResult = new ExceptionResult(exception);
            }
            return new TestMethodResult(testMethod.getMethodName(), exceptionResult);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
