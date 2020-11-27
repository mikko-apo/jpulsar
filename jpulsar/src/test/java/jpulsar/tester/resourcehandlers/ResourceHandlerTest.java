package jpulsar.tester.resourcehandlers;

import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.tester.SerialTester;
import jpulsar.tester.TestResultBuilder;
import jpulsar.tester.TestRunResult;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.Util.getPackagePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceHandlerTest {
    @Test
    public void testResourceResouceHandler() {
        TestScanResult scanResult = scanPackages(getPackagePath(getClass()), Scanner::collectTestScanResult);
        TestRunResult result = SerialTester.runTests(scanResult);

        assertEquals(asList("beforeAll", "before", "test", "after", "afterAll"), TestResource1.log);

        TestResultBuilder testResultBuilder = new TestResultBuilder();
        testResultBuilder.addTestClassResult(ResourceHandlers.class, "t1");
        testResultBuilder.compareTestResults(result);
    }
}
