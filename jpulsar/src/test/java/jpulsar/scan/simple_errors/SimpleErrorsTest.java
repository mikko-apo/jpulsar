package jpulsar.scan.simple_errors;

import jpulsar.scan.Scanner;
import jpulsar.scan.TestClass;
import jpulsar.scan.TestClassBuilder;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.ModifierEnum;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static jpulsar.scan.ScanErrors.invalidAttributes;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.ScannerTestUtil.emptyTestAnnotation;
import static jpulsar.util.Util.getPackagePath;

public class SimpleErrorsTest {
    @Test
    void simpleErrors() {
        TestScanResult result = scanPackages(getPackagePath(getClass()), Scanner::collectTestScanResult);
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
        builder.compareTestClasses(result.getTestClasses());
    }
}
