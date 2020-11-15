package jpulsar.scan;

import io.github.classgraph.AnnotationEnumValue;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.ScanResult;
import jpulsar.Test;
import jpulsar.TestResource;
import jpulsar.TestResourceScope;
import jpulsar.scan.annotationdata.TestAnnotationData;
import jpulsar.scan.annotationdata.TestResourceAnnotationData;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.util.NamedItem;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
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

    @SuppressWarnings("unchecked")
    static public TestScanResult collectTestClasses(ScanResult scanResult) {
        ClassInfoList classesWithTests = scanResult.getClassesWithMethodAnnotation(jpulsar.Test.class.getName());
        TestScanResult testScanResult = new TestScanResult();
        classesWithTests.stream().map(classInfo -> {
            Class<?> clazz = classInfo.loadClass();
            TestClass<?> testClass = (TestClass<?>) new TestClass(clazz);
            testScanResult.addTestClass(testClass);
            MethodInfoList constructors = classInfo.getConstructorInfo();
            boolean classHasIssues = false;
            if (classInfo.isAbstract()) {
                classHasIssues = true;
                testClass.addClassIssue("is abstract");
            }
            if( constructors.size() == 1) {
                testClass.setConstructorInfo(new ConstructorInfo(getParameterClassArray(scanResult, constructors.get(0))));
            } else if (constructors.size() > 1) {
                classHasIssues = true;
                testClass.addClassIssue("has " + constructors.size() + " constructors. Should have 0 or 1 constructor");
            }
            for (MethodInfo methodInfo : classInfo.getMethodInfo()) {
                AnnotationInfo testResourceAnnotation = methodInfo.getAnnotationInfo(TestResource.class.getName());
                boolean isTestResource = testResourceAnnotation != null;
                AnnotationInfo testAnnotation = methodInfo.getAnnotationInfo(Test.class.getName());
                boolean isTest = testAnnotation != null;
                boolean methodHasIssues = false;
                if (isTestResource && isTest) {
                    classHasIssues = methodHasIssues = true;
                    testClass.addMethodIssue(methodInfo, "has both @Test and @TestResource annotations. Can have only one.");
                }
                if (!methodInfo.hasBody()) {
                    classHasIssues = methodHasIssues = true;
                    testClass.addMethodIssue(methodInfo, "is abstract");
                }
                if (!methodHasIssues) {
                    if (isTest) {
                        testClass.addTestMethod(createTestMethod(scanResult, methodInfo, testAnnotation));
                    }
                    if (isTestResource) {
                        testClass.addTestResource(createTestResource(scanResult, methodInfo, testResourceAnnotation));
                    }
                }
            }
            return testClass;
        }).collect(toList());
        return testScanResult;
    }

    static private List<Class<?>> getParameterClassArray(ScanResult scanResult, MethodInfo methodInfo) {
        MethodParameterInfo[] parameterInfos = methodInfo.getParameterInfo();
        Class<?>[] arr = new Class<?>[parameterInfos.length];
        for (int i = 0; i < parameterInfos.length; i++) {
            MethodParameterInfo parameterInfo = parameterInfos[i];
            arr[i] = scanResult.loadClass(parameterInfo.getTypeDescriptor().toString(), false);
        }
        return asList(arr);
    }

    static private TestMethod createTestMethod(ScanResult scanResult, MethodInfo methodInfo, AnnotationInfo testAnnotation) {
        AnnotationParameterValueList annotationParameterValues = testAnnotation.getParameterValues();
        String name = (String) annotationParameterValues.getValue("name");
        String[] usecases = (String[]) annotationParameterValues.getValue("usecases");
        String[] tags = (String[]) annotationParameterValues.getValue("tags");
        return new TestMethod(
                methodInfo.getName(),
                getParameterClassArray(scanResult, methodInfo),
                new TestAnnotationData(name,
                        asList(usecases),
                        asList(tags)));
    }

    static private TestResourceMethod createTestResource(ScanResult scanResult, MethodInfo methodInfo, AnnotationInfo testResourceAnnotation) {
        AnnotationParameterValueList annotationParameterValues = testResourceAnnotation.getParameterValues();
        String name = (String) annotationParameterValues.getValue("name");
        Integer max = (Integer) annotationParameterValues.getValue("max");
        Boolean shared = (Boolean) annotationParameterValues.getValue("shared");
        Boolean fixed = (Boolean) annotationParameterValues.getValue("fixed");
        Boolean hidden = (Boolean) annotationParameterValues.getValue("hidden");
        TestResourceScope scope = (TestResourceScope) ((AnnotationEnumValue) annotationParameterValues.getValue("scope")).loadClassAndReturnEnumValue();
        String[] usecases = (String[]) annotationParameterValues.getValue("usecases");
        TestResourceMethod testResourceMethod = new TestResourceMethod(
                methodInfo.getName(),
                getParameterClassArray(scanResult, methodInfo),
                new TestResourceAnnotationData(name,
                        max,
                        shared,
                        fixed,
                        hidden,
                        scope,
                        asList(usecases)));
        boolean maxDefined = TestResourceAnnotationData.MaxDefault != max;
        List<NamedItem<Boolean>> enabledFeatures = Stream.of(new NamedItem<>("max", maxDefined),
                new NamedItem<>("shared", shared),
                new NamedItem<>("fixed", fixed)
        ).filter(booleanNamedItem -> booleanNamedItem.data).collect(toList());
        if (enabledFeatures.size() > 0) {
            testResourceMethod.addIssue("Can enable only one feature. Now has",
                    enabledFeatures.stream().map(booleanNamedItem -> booleanNamedItem.name).collect(Collectors.joining(", ")));
        }
        return testResourceMethod;
    }
}
