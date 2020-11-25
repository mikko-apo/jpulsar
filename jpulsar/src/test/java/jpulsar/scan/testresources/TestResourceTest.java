package jpulsar.scan.testresources;

import jpulsar.scan.Scanner;
import jpulsar.scan.TestClassBuilder;
import jpulsar.scan.TestScanResult;
import org.junit.jupiter.api.Test;

import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.ScannerTestUtil.emptyTestResourceAnnotation;
import static jpulsar.util.Util.getPackagePath;
import static jpulsar.util.Util.scannerJackson;

public class TestResourceTest {
    @Test
    void testResources() {
        TestScanResult result = scanPackages(getPackagePath(getClass()),
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
}
