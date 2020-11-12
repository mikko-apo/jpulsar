package jpulsar.scan;

import example.TestResources;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import jpulsar.TestResource;
import jpulsar.TestResourceScope;
import jpulsar.Usecase;
import jpulsar.scan.annotationdata.TestAnnotationData;
import jpulsar.scan.annotationdata.TestResourceAnnotationData;
import jpulsar.scan.simple_errors.AbstractClassTestMethod;
import jpulsar.scan.simple_errors.BothTestAndTestResource;
import jpulsar.scan.simple_errors.TooManyConstructors;
import jpulsar.scan.test_method.TestMethodTestResource;
import jpulsar.scan.test_method.TestMethodTestResource2;
import jpulsar.scan.test_method.TestMethods;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.test.Util.jsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerTest {
    public static <T> String getPackagePath(Class<T> clazz) {
        String[] full = clazz.getName().split("\\.");
        String[] packagePath = Arrays.copyOf(full, full.length - 1);

        return String.join(".", packagePath);
    }

    public static String getClassName(String path) {
        String[] full = path.split("\\.");
        return full[full.length - 1];
    }

    @Test
    public void simpleErrors() {
        TestScanResult result = scanPackages(getPackagePath(TooManyConstructors.class), Scanner::collectTestClasses);
        TestClass<AbstractClassTestMethod> t1 = new TestClass<>(AbstractClassTestMethod.class, asList(), asList());
        t1.getIssues().addAll(asList("jpulsar.scan.simple_errors.AbstractClassTestMethod is abstract",
                "jpulsar.scan.simple_errors.AbstractClassTestMethod.test() is abstract"));
        TestClass<BothTestAndTestResource> t2 = new TestClass<>(BothTestAndTestResource.class, asList(), asList());
        t2.getIssues().add("jpulsar.scan.simple_errors.BothTestAndTestResource.test() has both @Test and @TestResource annotations. Can have only one.");
        TestClass<TooManyConstructors> t3 = new TestClass<>(TooManyConstructors.class,
                asList(new TestMethod("test", asList(), new TestAnnotationData(null, asList(), asList()))),
                asList());
        t3.getIssues().add("jpulsar.scan.simple_errors.TooManyConstructors has 2 constructors. Should have 0 or 1 constructor");
        jsonEquals(asList(t1, t2, t3), result.testClasses);
    }

    @Test
    public void testMethodInitialization() {
        TestScanResult result = scanPackages(getPackagePath(TestMethods.class), Scanner::collectTestClasses);
        List<TestClass<?>> testClasses = result.testClasses;
        assertEquals(1, testClasses.size());
        TestClass<?> testClass = testClasses.get(0);
        jsonEquals(asList(new TestClass<>(TestMethods.class,
                asList(
                        new TestMethod("test0",
                                asList(),
                                new TestAnnotationData(null, asList(), asList())),
                        new TestMethod("test1",
                                asList(TestMethodTestResource.class),
                                new TestAnnotationData(null, asList(), asList())),
                        new TestMethod("testWithInvalidTestResource",
                                asList(TestMethodTestResource.class, TestMethodTestResource2.class),
                                new TestAnnotationData(null, asList(), asList()))
                ),
                asList(new TestResourceMethod("testResource",
                        asList(),
                        new TestResourceAnnotationData(null,
                                null,
                                false,
                                false,
                                false,
                                TestResourceScope.GLOBAL,
                                asList()))))), result.testClasses);
    }

    private List<String> getParameterTypeClassNames(List<Class<?>> methodParameterTypes) {
        return methodParameterTypes.stream().map(Class::getName).collect(toList());
    }


    @Test
    public void scanJPulsarAnnotations() {
        scanPackages(getPackagePath(TestResources.class), scanResult -> {
            // Perform the scan and return a ScanResult
            assertAnnotedMethods(scanResult, jpulsar.Test.class, asList(
                    "TicketSaleErrorsTest.ticketAlreadySold",
                    "TicketSaleMobileTest.sellSucceeds",
                    "TicketSaleTest.sellSucceeds"
            ));

            assertAnnotedMethods(scanResult, jpulsar.TestFactory.class, asList(
                    "ApiErrorsTest.createApiErrorTests"
            ));
            assertAnnotedMethods(scanResult, TestResource.class, asList(
                    "TestResources.httpClient",
                    "TestResources.testDb",
                    "TestResources.testDbClient",
                    "TestResources.testServer",
                    "TicketSaleMobileTest.mobileClient",
                    "TicketSaleMobileTest.mobileTester"
            ));
            String annotationName = Usecase.class.getName();
            ClassInfoList classesAnnotation = scanResult.getClassesWithAnnotation(annotationName);
            assertEquals(asList(
                    "ApiErrorsTest",
                    "TicketSaleErrorsTest",
                    "TicketSaleMobileTest",
                    "TicketSaleTest"
            ), classesAnnotation.stream().map(ClassInfo::getSimpleName).sorted().collect(toList()));
            return "";
        });
    }

    private <T> void assertAnnotedMethods(ScanResult scanResult, Class<T> clazz, List<String> expected) {
        Stream<MethodInfo> annotedMethods = getAnnotedMethods(scanResult, clazz);
        List<String> actual = annotedMethods.map(methodInfo -> methodInfo.getClassInfo().getSimpleName() + "." + methodInfo.getName()).sorted().collect(toList());
        assertEquals(expected, actual);
    }

    private <T> Stream<MethodInfo> getAnnotedMethods(ScanResult scanResult, Class<T> clazz) {
        String annotationName = clazz.getName();
        ClassInfoList classesWithMethodAnnotation = scanResult.getClassesWithMethodAnnotation(annotationName);
        return classesWithMethodAnnotation.stream()
                .map(classInfo -> classInfo.getMethodInfo().filter(methodInfo -> methodInfo.hasAnnotation(annotationName)))
                .flatMap(Collection::stream);
    }
}
