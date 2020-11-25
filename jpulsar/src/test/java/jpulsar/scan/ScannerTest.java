package jpulsar.scan;

import example.TestResources;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import jpulsar.TestResourceScope;
import jpulsar.Usecase;
import jpulsar.scan.method.ModifierEnum;
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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
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

        builder.addTestClass(AbstractClassTestMethod.class);
        builder.addTestMethod(emptyTestAnnotation, "test")
                .addIssue(invalidAttributes(asList(ModifierEnum.ABSTRACT)));
        builder.testClass.getConstructor().getIssues().add(invalidAttributes(asList(ModifierEnum.ABSTRACT)));

        builder.addTestClass(BothTestAndTestResource.class);
        builder.addTestMethod(emptyTestAnnotation, "test")
                .addIssue("has both @Test and @TestResource annotations. Can have only one.");

        TestClass<?> t3 = builder.addTestClass(TooManyConstructors.class);
        t3.getIssues().add("has 2 constructors. Should have 1 constructor");
        t3.setConstructor(null);
        builder.addTestMethod(emptyTestAnnotation, "test");
        scannerJackson.jsonEquals(builder.testClasses, result.getTestClasses());
    }

    @Test
    void testMethodInitialization() {
        TestScanResult result = scanPackages(getPackagePath(TestMethods.class), Scanner::collectTestClasses);
        TestClassBuilder builder = new TestClassBuilder();

        builder.addTestClass(TestMethods.class);
        TestResourceMethod tr1 = builder.addTestResourceMethod(emptyClassTestResourceAnnotation,
                "testResource",
                true);
        builder.addTestMethod(emptyTestAnnotation, "test0");
        builder.addTestMethod(emptyTestAnnotation, "test1", TestResource1.class)
                .addParameterTestResource(tr1);
        builder.addTestMethod(emptyTestAnnotation,
                "testWithInvalidTestResource",
                TestResource1.class,
                TestResource2.class)
                .addParameterTestResource(tr1)
                .addParameterTestResource(null)
                .addIssue(ScanErrors.noMatchingTestResources(1, null, TestResource2.class));

        scannerJackson.jsonEquals(builder.testClasses, result.getTestClasses());
    }

    private <T extends Issues> T addPrivateProtectedIssue(int index, T target) {
        if (asList(4, 8).contains(index)) {
            target.addIssue(invalidAttributes(asList(ModifierEnum.PRIVATE)));
        }
        if (asList(3, 7).contains(index)) {
            target.addIssue(invalidAttributes(asList(ModifierEnum.PROTECTED)));
        }
        return target;
    }

    @Test
    void testVisibility() {
        TestScanResult result = scanPackages(getPackagePath(VisibilityTest.class), Scanner::collectTestClasses);
        TestClassBuilder builder = new TestClassBuilder();

        List<ModifierEnum> modifiers = asList(
                null,
                null,
                ModifierEnum.PROTECTED,
                ModifierEnum.PRIVATE,
                null,
                null,
                ModifierEnum.PROTECTED,
                ModifierEnum.PRIVATE
        );

        builder.addTestClass(VisibilityTest.class)
                .setConstructor(null)
                .addIssue(ScanErrors.tooManyConstructors(new Constructor[4]));
        IntStream.range(1, 9).forEach(i -> {
            TestMethod t = builder.addTestMethod(emptyTestAnnotation, "test" + i);
            TestResourceMethod tr = builder.addTestResourceMethod(emptyClassTestResourceAnnotation, "tr" + i, true);
            ModifierEnum modifierEnum = modifiers.get(i - 1);
            if(modifierEnum != null) {
                String invalidAttribute = invalidAttributes(asList(modifierEnum));
                t.addIssue(invalidAttribute);
                tr.addIssue(invalidAttribute);
            }
        });
        scannerJackson.jsonEquals(builder.testClasses, result.getTestClasses());
    }

        @Test
        void testResources() {
            TestScanResult result = scanPackages(getPackagePath(jpulsar.scan.testresources.TestResources.class),
                    Scanner::collectTestClasses);
            TestClassBuilder builder = new TestClassBuilder();

            builder.addTestClass(jpulsar.scan.testresources.TestResources.class);
            builder.addTestResourceMethod(emptyTestResourceAnnotation, "tr1", false);
            builder.addTestResourceMethod(emptyTestResourceAnnotation, "tr2", false)
                    .addIssue("parametrized returnType is not supported java.util.function.Supplier<jpulsar.scan.resources.TestResource2>");
            builder.addTestResourceMethod(emptyTestResourceAnnotation, "tr3", false)
                    .addIssue("parametrized returnType is not supported java.util.function.Supplier<java.util.function.Supplier<jpulsar.scan.resources.TestResource3>>");
            scannerJackson.jsonEquals(builder.testClasses, result.getTestClasses());
        }

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
