package jpulsar.scan.testresources;

import jpulsar.scan.ScanErrors;
import jpulsar.scan.Scanner;
import jpulsar.scan.TestClassBuilder;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;
import jpulsar.scan.resources.TestResource3;
import jpulsar.scan.resources.TestResource4;
import jpulsar.scan.resources.TestResource5;
import org.junit.jupiter.api.Test;

import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.ScannerTestUtil.emptyTestAnnotation;
import static jpulsar.util.ScannerTestUtil.emptyTestResourceAnnotation;
import static jpulsar.util.Util.getPackagePath;

public class TestResourceTest {
    @Test
    void testResources() {
        TestScanResult result = scanPackages(getPackagePath(getClass()),
                Scanner::collectTestClasses);
        TestClassBuilder builder = new TestClassBuilder();

        builder.addTestClass(jpulsar.scan.testresources.TestResources.class);
        TestResourceMethod tr1 = builder.addTestResourceMethod(emptyTestResourceAnnotation, "tr1", true);

        TestResourceMethod tr2 = builder.addTestResourceMethod(emptyTestResourceAnnotation, "tr2", true);
        tr2.addIssue("parametrized returnType is not supported java.util.function.Supplier<jpulsar.scan.resources.TestResource2>");
        tr2.addIssue(ScanErrors.unusedTestResource());

        TestResourceMethod tr3 = builder.addTestResourceMethod(emptyTestResourceAnnotation, "tr3", true);
        tr3.addIssue("parametrized returnType is not supported java.util.function.Supplier<java.util.function.Supplier<jpulsar.scan.resources.TestResource3>>");
        tr3.addIssue(ScanErrors.unusedTestResource());

        TestResourceMethod tr4 = builder.addTestResourceMethod(emptyTestResourceAnnotation,
                "testResourceWithResourceHandler",
                true);

        TestResourceMethod tr5 = builder.addTestResourceMethod(emptyTestResourceAnnotation,
                "testResourceWithDependency",
                true,
                TestResource1.class);
        tr5.addIssue(ScanErrors.testResourceDependenciesNotSupported(tr5.getMethod().getGenericParameterTypes()));

        builder.addTestResourceMethod(emptyTestResourceAnnotation,
                "testResourceWithoutTestsUsingIt",
                true)
                .addIssue(ScanErrors.unusedTestResource());

        builder.addTestMethod(emptyTestAnnotation, "test0");

        builder.addTestMethod(emptyTestAnnotation, "test1", TestResource1.class)
                .addParameterTestResource(tr1);

        builder.addTestMethod(emptyTestAnnotation,
                "testWithInvalidTestResource",
                TestResource1.class,
                String.class)
                .addParameterTestResource(tr1)
                .addParameterTestResource(null)
                .addIssue(ScanErrors.noMatchingTestResources(1, null, String.class));

        builder.addTestMethod(emptyTestAnnotation, "testWithResourceHandler", TestResource4.class)
                .addParameterTestResource(tr4);

        builder.addTestMethod(emptyTestAnnotation, "testWithTestResourceWithDependency", TestResource5.class)
                .addParameterTestResource(tr5);

        builder.addTestMethod(emptyTestAnnotation, "testWithSameTestResourceTwice", TestResource1.class, TestResource1.class)
                .addParameterTestResource(tr1)
                .addParameterTestResource(tr1).addIssue(ScanErrors.sameTestResourceMoreThanOnce(1, TestResource1.class));

        builder.compareTestClasses(result.getTestClasses());
    }
}
