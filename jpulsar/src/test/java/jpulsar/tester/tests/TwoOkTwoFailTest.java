package jpulsar.tester.tests;

import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.tester.ExceptionResult;
import jpulsar.tester.SerialTester;
import jpulsar.tester.TestClassResult;
import jpulsar.tester.TestMethodResult;
import jpulsar.tester.TestRunResult;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static java.util.Arrays.asList;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.ScannerTestUtil.findTestMethodResult;
import static jpulsar.util.ScannerTestUtil.removeStacktraceDuration;
import static jpulsar.util.Util.getPackagePath;
import static jpulsar.util.Util.scannerJackson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TwoOkTwoFailTest {
    @Test
    void executeTwoOkTestMethodsAndTwoFailures() {
        TestScanResult scanInfo = scanPackages(getPackagePath(TwoOkTwoFail.class), Scanner::collectTestScanResult);
        TestRunResult result = SerialTester.runTests(scanInfo);
        TestRunResult expectedTestRunResult = new TestRunResult(asList(
                new TestClassResult<>(TwoOkTwoFail.class, asList(
                        new TestMethodResult("ok1", null, null, asList()),
                        new TestMethodResult("ok2", null, null, asList()),
                        new TestMethodResult("fail1",
                                new ExceptionResult(new RuntimeException("should be caught")),
                                null, asList()),
                        new TestMethodResult("fail2",
                                new ExceptionResult(new AssertionFailedError("expected: <1> but was: <2>")),
                                null, asList())
                ))
        ));
        assertTrue(findTestMethodResult(result, "ok1").getDurationMs() >= 10);
        removeStacktraceDuration(result);
        removeStacktraceDuration(expectedTestRunResult);
        scannerJackson.jsonEquals(expectedTestRunResult, result);
        assertEquals(4, TwoOkTwoFail.testCount);
        assertEquals(15, TwoOkTwoFail.counter);
    }
}
