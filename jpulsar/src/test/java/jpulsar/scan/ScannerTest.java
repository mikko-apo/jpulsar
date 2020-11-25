package jpulsar.scan;

import example.TestResources;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import jpulsar.TestResourceScope;
import jpulsar.Usecase;
import jpulsar.scan.method.ModifierHelper;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;
import jpulsar.scan.simple_errors.AbstractClassTestMethod;
import jpulsar.scan.simple_errors.BothTestAndTestResource;
import jpulsar.scan.simple_errors.TooManyConstructors;
import jpulsar.scan.test_method.TestMethods;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static jpulsar.scan.ScanErrors.invalidAttributes;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.Collections.toList;
import static jpulsar.util.Util.scannerJackson;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerTest {

    private final jpulsar.Test emptyTestAnnotation = TestClassBuilder.createTestAnnotation("", new String[0], new String[0]);
    private final jpulsar.TestResource emptyTestResourceAnnotation = TestClassBuilder.createTestResourceAnnotation("",
            0,
            false,
            false,
            false,
            TestResourceScope.GLOBAL,
            new String[0]
            );
    private final jpulsar.TestResource emptyClassTestResourceAnnotation = TestClassBuilder.createTestResourceAnnotation("",
            0,
            false,
            false,
            false,
            TestResourceScope.CLASS,
            new String[0]
    );

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
    void simpleErrors() {
        TestScanResult result = scanPackages(getPackagePath(TooManyConstructors.class), Scanner::collectTestClasses);
        TestClassBuilder builder = new TestClassBuilder();

        TestClass<?> t1 = builder.build(AbstractClassTestMethod.class);
        builder.addTestMethod(emptyTestAnnotation, "test")
                .addIssue(invalidAttributes(asList(ModifierHelper.ABSTRACT)));
        builder.testClass.getConstructor().getIssues().add(invalidAttributes(asList(ModifierHelper.ABSTRACT)));

        TestClass<?> t2 = builder.build(BothTestAndTestResource.class);
        builder.addTestMethod(emptyTestAnnotation, "test")
                .addIssue("has both @Test and @TestResource annotations. Can have only one.");

        TestClass<?> t3 = builder.build(TooManyConstructors.class);
        t3.getIssues().add("has 2 constructors. Should have 1 constructor");
        t3.setConstructor(null);
        builder.addTestMethod(emptyTestAnnotation, "test");
        scannerJackson.jsonEquals(asList(t1, t2, t3), result.getTestClasses());
    }

    @Test
    void testMethodInitialization() {
        TestScanResult result = scanPackages(getPackagePath(TestMethods.class), Scanner::collectTestClasses);
        TestClassBuilder builder = new TestClassBuilder();

        TestClass<?> t1 = builder.build(TestMethods.class);
        builder.addTestMethod(emptyTestAnnotation, "test0");
        builder.addTestMethod(emptyTestAnnotation, "test1", TestResource1.class);
        builder.addTestMethod(emptyTestAnnotation,
                "testWithInvalidTestResource",
                TestResource1.class,
                TestResource2.class);
        builder.addTestResourceMethod(emptyClassTestResourceAnnotation, "testResource", true);

        scannerJackson.jsonEquals(asList(t1), result.getTestClasses());
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
/*
    @Test
    void testVisibility() {
        TestScanResult result = scanPackages(getPackagePath(VisibilityTest.class), Scanner::collectTestClasses);
        List<Integer> modifiers = asList(0, 9, 8, 12, 10, 1, 0, 4, 2);
        Counter trCounter = new Counter(0);
        List<Class<?>> trReturnType = asList(TestResource1.class,
                TestResource2.class,
                TestResource3.class,
                TestResource4.class,
                TestResource5.class,
                TestResource6.class,
                TestResource7.class,
                TestResource8.class
        );
        TestClass<VisibilityTest> visibilityTestTestClass = new TestClass<>(VisibilityTest.class,
                null,
                toList(IntStream.range(1, 9).mapToObj(i -> addPrivateProtectedIssue(i, new TestMethod("test" + i,
                        modifiers.get(i),
                        emptyParameters,
                        emptyTestAnnotation))
                )),
                toList(IntStream.range(1, 9).mapToObj(i -> addPrivateProtectedIssue(i, new TestResourceMethod("tr" + i,
                        modifiers.get(i),
                        emptyParameters,
                        new TypeSignature(trReturnType.get(trCounter.postfixIncrement()), asList()),
                        emptyClassTestResourceAnnotation))
                ))
        );
        visibilityTestTestClass.getIssues().add(VisibilityTest.class.getName() + " has 4 constructors. Should have 0 or 1 constructor");
        jsonEquals(asList(visibilityTestTestClass), result.getTestClasses());
    }

    @Test
    void testResources() {
        TestScanResult result = scanPackages(getPackagePath(jpulsar.scan.testresources.TestResources.class),
                Scanner::collectTestClasses);
        TestClass<jpulsar.scan.testresources.TestResources> testResources =
                new TestClass<>(jpulsar.scan.testresources.TestResources.class,
                        emptyConstructorInfo,
                        asList(),
                        asList(
                                new TestResourceMethod("tr1",
                                        1,
                                        emptyParameters,
                                        new TypeSignature(TestResource1.class, asList()),
                                        emptyTestResourceAnnotation),
                                new TestResourceMethod("tr2",
                                        1,
                                        emptyParameters,
                                        new TypeSignature(Supplier.class,
                                                asList(new TypeSignature(TestResource2.class, asList()))),
                                        emptyTestResourceAnnotation),
                                new TestResourceMethod("tr3",
                                        1,
                                        emptyParameters,
                                        new TypeSignature(Supplier.class,
                                                asList(new TypeSignature(Supplier.class,
                                                        asList(new TypeSignature(TestResource3.class, asList()))))),
                                        emptyTestResourceAnnotation)
                        ));
        jsonEquals(asList(testResources), result.getTestClasses());
    }

    private List<String> getParameterTypeClassNames(List<Class<?>> methodParameterTypes) {
        return map(methodParameterTypes, Class::getName);
    }
*/
    @Test
    void scanJPulsarAnnotations() {
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
            ), toList(classesAnnotation.stream().map(ClassInfo::getSimpleName).sorted()));
            return "";
        });
    }

    private <T> void assertAnnotedMethods(ScanResult scanResult, Class<T> clazz, List<String> expected) {
        Stream<MethodInfo> annotedMethods = getAnnotedMethods(scanResult, clazz);
        Stream<String> actual = annotedMethods.map(methodInfo -> methodInfo.getClassInfo().getSimpleName() + "." + methodInfo.getName()).sorted();
        assertEquals(expected, toList(actual));
    }

    private <T> Stream<MethodInfo> getAnnotedMethods(ScanResult scanResult, Class<T> clazz) {
        String annotationName = clazz.getName();
        ClassInfoList classesWithMethodAnnotation = scanResult.getClassesWithMethodAnnotation(annotationName);
        return classesWithMethodAnnotation.stream()
                .map(classInfo -> classInfo.getMethodInfo().filter(methodInfo -> methodInfo.hasAnnotation(annotationName)))
                .flatMap(Collection::stream);
    }
}
