package jpulsar.scan;

import io.github.classgraph.AnnotationEnumValue;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.MethodTypeSignature;
import io.github.classgraph.ScanResult;
import jpulsar.Test;
import jpulsar.TestResource;
import jpulsar.TestResourceScope;
import jpulsar.scan.annotationdata.TestAnnotationData;
import jpulsar.scan.annotationdata.TestResourceAnnotationData;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.MethodParameterInfo;
import jpulsar.scan.method.TypeSignature;
import jpulsar.scan.method.ModifierHelper;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestMethodBase;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.util.NamedItem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static jpulsar.scan.ScanErrors.invalidAttributes;
import static jpulsar.util.Streams.filter;
import static jpulsar.util.Streams.map;
import static jpulsar.util.Strings.mapJoin;

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
//                             .ignoreParentClassLoaders()
//                             .overrideClassLoaders(new URLClassLoaderHandler())
                             .scan()) {

            return processor.apply(scanResult);
        }
    }

    @SuppressWarnings("unchecked")
    static public TestScanResult collectTestClasses(ScanResult scanResult) {
        ClassInfoList classesWithTests = scanResult.getClassesWithMethodAnnotation(jpulsar.Test.class.getName());
        ClassInfoList classesWithResources = scanResult.getClassesWithMethodAnnotation(jpulsar.TestResource.class.getName());
        LinkedHashSet<ClassInfo> allClasses = new LinkedHashSet<>();
        allClasses.addAll(classesWithTests);
        allClasses.addAll(classesWithResources);
        TestScanResult testScanResult = new TestScanResult();
        for (ClassInfo classInfo : allClasses) {
            Class<?> clazz = classInfo.loadClass();
            TestClass<?> testClass = new TestClass<>(clazz);
            testScanResult.addTestClass(testClass);
            MethodInfoList constructors = classInfo.getConstructorInfo();
            ensureCorrectModifiers(testClass, clazz.getModifiers());
            if (constructors.size() == 1) {
                MethodInfo methodInfo = constructors.get(0);
                testClass.setConstructorInfo(new ConstructorInfo(methodInfo.getModifiers(), resolveParameterTypeSignatures(scanResult, methodInfo)));
            } else if (constructors.size() > 1) {
                testClass.addClassIssue("has " + constructors.size() + " constructors. Should have 0 or 1 constructor");
            }
            MethodInfoList methodInfoList = classInfo.getMethodInfo();
            processMethods(scanResult, methodInfoList, testClass);
        }
        return testScanResult;
    }

    private static void processMethods(ScanResult scanResult, MethodInfoList methodInfoList, TestClass<?> testClass) {
        for (MethodInfo methodInfo : methodInfoList) {
            AnnotationInfo testResourceAnnotation = methodInfo.getAnnotationInfo(TestResource.class.getName());
            boolean isTestResource = testResourceAnnotation != null;
            AnnotationInfo testAnnotation = methodInfo.getAnnotationInfo(Test.class.getName());
            boolean isTest = testAnnotation != null;
            if (isTest || isTestResource) {
                TestMethodBase method;
                if (isTest) {
                    TestMethod testMethod = createTestMethod(scanResult, methodInfo, testAnnotation);
                    testClass.addTestMethod(testMethod);
                    method = testMethod;
                } else {
                    TestResourceMethod testResource = createTestResource(scanResult, methodInfo, testResourceAnnotation);
                    testClass.addTestResource(testResource);
                    method = testResource;
                }
                ensureCorrectModifiers(method, methodInfo.getModifiers());
                if (isTestResource && isTest) {
                    method.addIssue("has both @Test and @TestResource annotations. Can have only one.");
                }
            }
        }
    }

    static private MethodParameterInfo resolveParameterTypeSignatures(ScanResult scanResult, MethodInfo methodInfo) {
        io.github.classgraph.MethodParameterInfo[] parameterInfos = methodInfo.getParameterInfo();
        Class<?>[] arr = new Class<?>[parameterInfos.length];
        List<TypeSignature> typeSignatures = new ArrayList<>();
        for (int i = 0; i < parameterInfos.length; i++) {
            io.github.classgraph.MethodParameterInfo parameterInfo = parameterInfos[i];
            arr[i] = scanResult.loadClass(parameterInfo.getTypeDescriptor().toString(), false);
            typeSignatures.add(resolveTypeSignature(parameterInfo.getTypeSignatureOrTypeDescriptor()));
        }
        return new MethodParameterInfo(arr, typeSignatures);
    }

    static private TestMethod createTestMethod(ScanResult scanResult, MethodInfo methodInfo, AnnotationInfo testAnnotation) {
        AnnotationParameterValueList annotationParameterValues = testAnnotation.getParameterValues();
        String name = (String) annotationParameterValues.getValue("name");
        String[] usecases = (String[]) annotationParameterValues.getValue("usecases");
        String[] tags = (String[]) annotationParameterValues.getValue("tags");
        return new TestMethod(
                methodInfo.getName(),
                methodInfo.getModifiers(),
                resolveParameterTypeSignatures(scanResult, methodInfo),
                new TestAnnotationData(name,
                        asList(usecases),
                        asList(tags)));
    }

    private static void ensureCorrectModifiers(Issues target, int modifiers) {
        List<ModifierHelper> foundInvalidModifiers = ModifierHelper.hasModifiers(modifiers,
                ModifierHelper.PRIVATE,
                ModifierHelper.PROTECTED,
                ModifierHelper.ABSTRACT);
        if (foundInvalidModifiers.size() > 0) {
            target.addIssue(invalidAttributes(foundInvalidModifiers));
        }
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
        MethodTypeSignature methodTypeSignature = methodInfo.getTypeSignatureOrTypeDescriptor();
        TestResourceMethod testResourceMethod = new TestResourceMethod(
                methodInfo.getName(),
                methodInfo.getModifiers(),
                resolveParameterTypeSignatures(scanResult, methodInfo),
                resolveTypeSignature(methodTypeSignature.getResultType()),
                new TestResourceAnnotationData(name,
                        max,
                        shared,
                        fixed,
                        hidden,
                        scope,
                        asList(usecases)));
        boolean maxDefined = TestResourceAnnotationData.MaxDefault != max;
        List<NamedItem<Boolean>> enabledFeatures = filter(asList(
                new NamedItem<>("max", maxDefined),
                new NamedItem<>("shared", shared),
                new NamedItem<>("fixed", fixed)
        ), booleanNamedItem -> booleanNamedItem.data);
        if (enabledFeatures.size() > 0) {
            testResourceMethod.addIssue("Can enable only one feature. Now has", mapJoin(enabledFeatures, booleanNamedItem -> booleanNamedItem.name, ", "));
        }
        return testResourceMethod;
    }

    private static TypeSignature resolveTypeSignature(io.github.classgraph.TypeSignature typeSignature) {
        ClassRefTypeSignature classRefTypeSignature = (ClassRefTypeSignature) typeSignature;
        return new TypeSignature(classRefTypeSignature.loadClass(),
                map(classRefTypeSignature.getTypeArguments(),
                        typeArgument -> resolveTypeSignature(typeArgument.getTypeSignature())));
    }
}
