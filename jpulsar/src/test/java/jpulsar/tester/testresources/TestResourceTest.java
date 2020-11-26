package jpulsar.tester.testresources;


import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.tester.SerialTester;
import jpulsar.tester.TestClassResult;
import jpulsar.tester.TestMethodResult;
import jpulsar.tester.TestRunResult;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.ScannerTestUtil.removeStacktraceDuration;
import static jpulsar.util.Util.getPackagePath;
import static jpulsar.util.Util.scannerJackson;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestResourceTest {
    @Test
    void testResourceResolving() {
        TestScanResult scanResult = scanPackages(getPackagePath(getClass()), Scanner::collectTestScanResult);
        TestRunResult result = SerialTester.runTests(scanResult);
        TestRunResult expectedTestRunResult = new TestRunResult(asList(
                new TestClassResult<>(TestResources.class, asList(
                        new TestMethodResult("testTwoTestResources",
                                null,
                                0,
                                asList())))));
        removeStacktraceDuration(expectedTestRunResult);
        removeStacktraceDuration(result);
        scannerJackson.jsonEquals(expectedTestRunResult, result);
        assertEquals(1, TestResource1.count);
        assertEquals(2, TestResource2.count);
    }
}
