package jpulsar.scan;

import example.TestResources;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import jpulsar.TestResourceScope;
import jpulsar.Usecase;
import jpulsar.scan.annotationdata.TestAnnotationData;
import jpulsar.scan.annotationdata.TestResourceAnnotationData;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.ModifierHelper;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;
import jpulsar.scan.simple_errors.AbstractClassTestMethod;
import jpulsar.scan.simple_errors.BothTestAndTestResource;
import jpulsar.scan.simple_errors.TooManyConstructors;
import jpulsar.scan.test_method.TestMethods;
import jpulsar.scan.visibility.VisibilityTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static jpulsar.scan.ScanErrors.invalidAttributes;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.Util.jsonEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerTest {

    private final TestAnnotationData emptyTestAnnotation = new TestAnnotationData(null, asList(), asList());

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
        TestMethod abstractTestMethod = new TestMethod("test", 1024, asList(), emptyTestAnnotation);
        abstractTestMethod.addIssue(invalidAttributes(asList(ModifierHelper.ABSTRACT)));
        TestClass<AbstractClassTestMethod> t1 = new TestClass<>(AbstractClassTestMethod.class,
                new ConstructorInfo(1, asList()),
                asList(abstractTestMethod),
                asList());
        t1.getIssues().add(invalidAttributes(asList(ModifierHelper.ABSTRACT)));

        TestMethod twoAnnotationsTestMethod = new TestMethod("test", 0, asList(), emptyTestAnnotation);
        twoAnnotationsTestMethod.addIssue("has both @Test and @TestResource annotations. Can have only one.");
        TestClass<BothTestAndTestResource> t2 = new TestClass<>(BothTestAndTestResource.class, new ConstructorInfo(1, asList()), asList(twoAnnotationsTestMethod), asList());

        TestClass<TooManyConstructors> t3 = new TestClass<>(TooManyConstructors.class,
                null, asList(new TestMethod("test", 0, asList(), emptyTestAnnotation)),
                asList());
        t3.getIssues().add("jpulsar.scan.simple_errors.TooManyConstructors has 2 constructors. Should have 0 or 1 constructor");
        jsonEquals(asList(t1, t2, t3), result.getTestClasses());
    }

    @Test
    public void testMethodInitialization() {
        TestScanResult result = scanPackages(getPackagePath(TestMethods.class), Scanner::collectTestClasses);
        jsonEquals(asList(new TestClass<>(TestMethods.class,
                new ConstructorInfo(1, asList()),
                asList(
                        new TestMethod("test0",
                                0, asList(),
                                emptyTestAnnotation),
                        new TestMethod("test1",
                                0, asList(TestResource1.class),
                                emptyTestAnnotation),
                        new TestMethod("testWithInvalidTestResource",
                                0, asList(TestResource1.class, TestResource2.class),
                                emptyTestAnnotation)
                ),
                asList(new TestResourceMethod("testResource",
                        0, asList(),
                        new TestResourceAnnotationData(null,
                                null,
                                false,
                                false,
                                false,
                                TestResourceScope.GLOBAL,
                                asList()))))), result.getTestClasses());
    }

    private <T extends Issues> T addPrivateProtectedIssue(int index, T target) {
        if (asList(4, 8).contains(index)) {
            target.addIssue(invalidAttributes(asList(ModifierHelper.PRIVATE)));
        }
        if (asList(3, 7).contains(index)) {
            target.addIssue(invalidAttributes(asList(ModifierHelper.PROTECTED)));
        }
        return target;
    }

    @Test
    public void testVisibility() {
        TestScanResult result = scanPackages(getPackagePath(VisibilityTest.class), Scanner::collectTestClasses);
        List<Integer> modifiers = asList(0, 9, 8, 12, 10, 1, 0, 4, 2);
        TestClass<VisibilityTest> visibilityTestTestClass = new TestClass<>(VisibilityTest.class,
                null,
                IntStream.range(1, 9).mapToObj(i -> addPrivateProtectedIssue(i, new TestMethod("test" + i,
                        modifiers.get(i), asList(),
                        emptyTestAnnotation))
                ).collect(toList()),
                IntStream.range(1, 9).mapToObj(i -> addPrivateProtectedIssue(i, new TestResourceMethod("tr" + i,
                        modifiers.get(i), asList(),
                        new TestResourceAnnotationData(null,
                                0,
                                false,
                                false,
                                false,
                                TestResourceScope.GLOBAL,
                                asList())))
                ).collect(toList())
        );
        visibilityTestTestClass.getIssues().add(VisibilityTest.class.getName() + " has 4 constructors. Should have 0 or 1 constructor");
        jsonEquals(asList(visibilityTestTestClass), result.getTestClasses());
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
            assertAnnotedMethods(scanResult, jpulsar.TestResource.class, asList(
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
