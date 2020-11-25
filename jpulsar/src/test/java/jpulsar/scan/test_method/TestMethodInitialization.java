package jpulsar.scan.test_method;

import jpulsar.scan.ScanErrors;
import jpulsar.scan.Scanner;
import jpulsar.scan.TestClassBuilder;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;
import org.junit.jupiter.api.Test;

import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.ScannerTestUtil.emptyClassTestResourceAnnotation;
import static jpulsar.util.ScannerTestUtil.emptyTestAnnotation;
import static jpulsar.util.Util.getPackagePath;
import static jpulsar.util.Util.scannerJackson;

public class TestMethodInitialization {
    @Test
    void testMethodInitialization() {
        TestScanResult result = scanPackages(getPackagePath(getClass()), Scanner::collectTestClasses);
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
}
