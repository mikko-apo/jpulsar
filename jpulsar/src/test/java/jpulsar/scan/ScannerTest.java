package jpulsar.scan;

import example.TestResources;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import jpulsar.TestFactory;
import jpulsar.TestResource;
import jpulsar.Usecase;
import jpulsar.scan.simple_errors.TooManyConstructors;
import jpulsar.scan.test_method.TestMethods;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static jpulsar.scan.Scanner.scanPackages;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        assertEquals(asList("jpulsar.scan.simple_errors.AbstractClassTestMethod is abstract",
                "jpulsar.scan.simple_errors.AbstractClassTestMethod.test() is abstract",
                "jpulsar.scan.simple_errors.BothTestAndTestResource.test() has both @Test and @TestResource annotations. Can have only one.",
                "jpulsar.scan.simple_errors.TooManyConstructors has 2 constructors. Should have 0 or 1 constructor"), result.getIssues());
/*
        Set<Class<?>> usecaseClasses = reflections.getTypesAnnotatedWith(Usecase.class);
*/
    }

    @Test
    public void testMethodInitialization() {
        TestScanResult result = scanPackages(getPackagePath(TestMethods.class), Scanner::collectTestClasses);
        List<TestClass<?>> testClasses = result.testClasses;
        assertEquals(1, testClasses.size());
        TestClass<?> testClass = testClasses.get(0);
        List<TestMethod> testMethods = testClass.getTestMethods();
        assertEquals(3, testMethods.size());
        boolean tested0 = false;
        boolean tested1 = false;
        boolean tested2 = false;
        for (TestMethod t : testMethods) {
            List<String> parameterNames = Arrays.stream(t.getParameters()).map(Class::getName).collect(toList());
            switch (parameterNames.size()) {
                case 0:
                    tested0 = true;
                    break;
                case 1:
                    tested1 = true;
                    assertEquals(asList("jpulsar.scan.test_method.TestMethodTestResource"), parameterNames);
                    break;
                case 2:
                    tested2 = true;
                    assertEquals(asList("jpulsar.scan.test_method.TestMethodTestResource",
                            "jpulsar.scan.test_method.TestMethodTestResource2"), parameterNames);
                    break;
                default:
                    fail("There should be only 0, 1 and 2 parameters, but was: " + String.join(",", parameterNames));
                    break;
            }
        }
        assertEquals(asList(true, true, true), asList(tested0, tested1, tested2));
/*
        Set<Class<?>> usecaseClasses = reflections.getTypesAnnotatedWith(Usecase.class);
*/
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

            assertAnnotedMethods(scanResult, TestFactory.class, asList(
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
