package jpulsar.scan;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.ScanResult;
import jpulsar.TestResource;

import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class Scanner {
    static public <R> R scanPackages(String packagePath, Function<ScanResult, R> processor) {
        try (ScanResult scanResult =                // Assign scanResult in try-with-resources
                     new ClassGraph()                    // Create a new ClassGraph instance
//                             .verbose()                      // If you want to enable logging to stderr
                             .enableAllInfo()                // Scan classes, methods, fields, annotations
                             .acceptPackages(packagePath)   // Scan com.xyz and subpackages
                             .disableJarScanning()
//                             .disableDirScanning()
                             .disableModuleScanning()
                             .disableNestedJarScanning()
                             .ignoreParentClassLoaders()
//                             .overrideClassLoaders(new URLClassLoaderHandler())
                             .scan()) {

            return processor.apply(scanResult);
        }
    }

    static public TestScanResult collectTestClasses(ScanResult scanResult) {
        ClassInfoList classesWithTests = scanResult.getClassesWithMethodAnnotation(jpulsar.Test.class.getName());
        TestScanResult testScanResult = new TestScanResult();
        classesWithTests.stream().map( classInfo -> {
            Class<?> clazz = classInfo.loadClass();
            TestClass testClass = new TestClass(clazz);
            testScanResult.addTestClass(testClass);
            MethodInfoList constructors = classInfo.getConstructorInfo();
            boolean classHasIssues = false;
            if(classInfo.isAbstract()) {
                classHasIssues = true;
                testClass.addClassIssue("is abstract");
            }
            if(constructors.size() > 1) {
                classHasIssues = true;
                testClass.addClassIssue("has " + constructors.size() + " constructors. Should have 0 or 1 constructor");
            }
            for(MethodInfo methodInfo : classInfo.getMethodInfo()) {
                boolean isTestResource = methodInfo.hasAnnotation(TestResource.class.getName());
                boolean isTest = methodInfo.hasAnnotation(jpulsar.Test.class.getName());
                boolean methodHasIssues = false;
                if(isTestResource && isTest) {
                    classHasIssues = methodHasIssues = true;
                    testClass.addMethodIssue(methodInfo, "has both @Test and @TestResource annotations. Can have only one.");
                }
                if(!methodInfo.hasBody()) {
                    classHasIssues = methodHasIssues = true;
                    testClass.addMethodIssue(methodInfo, "is abstract");
                }
                if(!methodHasIssues) {
                    if(isTest) {
                        testClass.addTestMethod(createTestMethod(scanResult, methodInfo));
                    }
                    if(isTestResource) {
                        testClass.addTestResource(methodInfo);
                    }
                }
            }
            return testClass;
        }).collect(toList());
        return testScanResult;
    }

    static private Class<?>[] getParameterClassArray(ScanResult scanResult, MethodInfo methodInfo) {
        MethodParameterInfo[] parameterInfos = methodInfo.getParameterInfo();
        Class<?>[] arr= new Class<?>[parameterInfos.length];
        for (int i = 0; i < parameterInfos.length; i++) {
            MethodParameterInfo parameterInfo = parameterInfos[i];
            arr[i] = scanResult.loadClass(parameterInfo.getTypeDescriptor().toString(), false);
        }
        return arr;
    }

    static private TestMethod createTestMethod(ScanResult scanResult, MethodInfo methodInfo) {
        return new TestMethod(methodInfo.getName(), getParameterClassArray(scanResult, methodInfo));
    }
}
