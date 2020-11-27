package jpulsar.tester.testresources;


import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.tester.SerialTester;
import jpulsar.tester.TestResultBuilder;
import jpulsar.tester.TestRunResult;
import org.junit.jupiter.api.Test;

import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.Util.getPackagePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestResourceTest {
    @Test
    void testResourceResolving() {
        TestScanResult scanResult = scanPackages(getPackagePath(getClass()), Scanner::collectTestScanResult);
        TestRunResult result = SerialTester.runTests(scanResult);

        assertEquals(1, TestResource1.count);
        assertEquals(2, TestResource2.count);

        TestResultBuilder testResultBuilder = new TestResultBuilder();
        testResultBuilder.addTestClassResult(TestResources.class, "testTwoTestResources");
        testResultBuilder.compareTestResults(result);
    }
}
