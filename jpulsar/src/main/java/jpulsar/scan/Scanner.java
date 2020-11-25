package jpulsar.scan;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import jpulsar.Test;
import jpulsar.TestResource;
import jpulsar.TestResourceScope;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.ModifierHelper;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.util.NamedItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static jpulsar.scan.ScanErrors.invalidAttributes;
import static jpulsar.util.Collections.filter;
import static jpulsar.util.Collections.map;

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

    static public TestScanResult collectTestClasses(ScanResult scanResult) {
        TestScanResult testScanResult = new TestScanResult();
        LinkedHashSet<ClassInfo> classesWithTests = new LinkedHashSet<>(
                scanResult.getClassesWithMethodAnnotation(jpulsar.Test.class.getName())
        );
        ClassInfoList classesWithResources = scanResult.getClassesWithMethodAnnotation(jpulsar.TestResource.class.getName());
        LinkedHashSet<ClassInfo> allClasses = new LinkedHashSet<>();
        allClasses.addAll(classesWithTests);
        allClasses.addAll(classesWithResources);

        for (ClassInfo classInfo : allClasses) {
            Class<?> aClass = classInfo.loadClass();
            TestClass<?> testClass = new TestClass<>(aClass);
            testScanResult.addTestClass(testClass);

            checkConstructors(aClass.getConstructors(), testClass);

            boolean testResourcesHaveClassScope = classesWithTests.contains(classInfo);
            Set<Method> methods = new LinkedHashSet<>();
            methods.addAll(asList(aClass.getMethods()));
            methods.addAll(asList(aClass.getDeclaredMethods()));
            processMethods(methods, testClass, testResourcesHaveClassScope);

            List<TestResourceMethod> globalTestResourceMethods = filter(testClass.getTestResources(),
                    testResourceMethod -> testResourceMethod.scope() == TestResourceScope.GLOBAL);
            testScanResult.getGlobalTestResourceMethods().addAll(globalTestResourceMethods);
        }
        return testScanResult;
    }

    private static void checkConstructors(Constructor<?>[] constructors, TestClass<?> testClass) {
        if (constructors.length == 1) {
            Constructor<?> constructor = constructors[0];
            ConstructorInfo constructorInfo = new ConstructorInfo(constructor);
            testClass.setConstructor(constructorInfo);
            checkParameterTypeSignatures(constructorInfo, constructor.getGenericParameterTypes());
            checkCorrectModifiers(constructorInfo, testClass.getClazz().getModifiers());
        } else if (constructors.length > 1) {
            testClass.addIssue(ScanErrors.tooManyConstructors(constructors));
        }
    }

    private static void processMethods(Set<Method> methods,
                                       TestClass<?> testClass,
                                       boolean testResourcesHaveClassScope) {
        for (Method method : methods) {
            TestResource testResourceAnnotation = method.getAnnotation(TestResource.class);
            boolean isTestResource = testResourceAnnotation != null;
            Test testAnnotation = method.getAnnotation(Test.class);
            boolean isTest = testAnnotation != null;
            if (isTest || isTestResource) {
                Issues target;
                if (isTest) {
                    TestMethod testMethod = new TestMethod(method, testAnnotation);
                    testClass.addTestMethod(testMethod);
                    checkParameterTypeSignatures(testMethod, method.getTypeParameters());
                    checkCorrectModifiers(testMethod, method.getModifiers());
                    target = testMethod;
                } else {
                    TestResourceMethod testResource = createTestResource(
                            method,
                            testResourceAnnotation,
                            testResourcesHaveClassScope);
                    testClass.addTestResource(testResource);
                    checkParameterTypeSignatures(testResource, method.getTypeParameters());
                    checkCorrectModifiers(testResource, method.getModifiers());
                    target = testResource;
                }
                if (isTestResource && isTest) {
                    target.addIssue("has both @Test and @TestResource annotations. Can have only one.");
                }
            }
        }
    }

    static private void checkParameterTypeSignatures(Issues target, Type[] genericParameterTypes) {
        for (int i = 0; i < genericParameterTypes.length; i++) {
            Type parameterType = genericParameterTypes[i];
            if (parameterType instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) parameterType;
                List<String> params = map(t.getActualTypeArguments(), Type::getTypeName);
                target.addIssue(ScanErrors.parametrizedArgument(i, params));
            } else if (parameterType instanceof Class) {
            } else {
                target.addIssue(ScanErrors.invalidParameter(i, parameterType));
            }
        }
    }

    private static void checkCorrectModifiers(Issues target, int modifiers) {
        List<ModifierHelper> foundInvalidModifiers = ModifierHelper.hasModifiers(modifiers,
                ModifierHelper.PRIVATE,
                ModifierHelper.PROTECTED,
                ModifierHelper.ABSTRACT);
        if (foundInvalidModifiers.size() > 0) {
            target.addIssue(invalidAttributes(foundInvalidModifiers));
        }
    }

    static private TestResourceMethod createTestResource(Method method,
                                                         TestResource testResourceAnnotation,
                                                         boolean testResourcesHaveClassScope) {
        TestResourceMethod testResourceMethod = new TestResourceMethod(method,
                testResourceAnnotation,
                testResourcesHaveClassScope);
        boolean maxDefined = TestResourceMethod.MaxDefault != testResourceAnnotation.max();
        List<NamedItem<Boolean>> enabledFeatures = filter(asList(
                new NamedItem<>("max", maxDefined),
                new NamedItem<>("shared", testResourceAnnotation.shared()),
                new NamedItem<>("fixed", testResourceAnnotation.fixed())
        ), booleanNamedItem -> booleanNamedItem.data);
        if (enabledFeatures.size() > 0) {
            testResourceMethod.addIssue(ScanErrors.tooManyFeatures(enabledFeatures));
        }
        return testResourceMethod;
    }
}
