package jpulsar.tester.tests;

import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.tester.ClassResult;
import jpulsar.tester.ExceptionResult;
import jpulsar.tester.SerialTester;
import jpulsar.tester.TestResultBuilder;
import jpulsar.tester.TestRunResult;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.tester.TestResultBuilder.findTestMethodResult;
import static jpulsar.util.Util.getPackagePath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TwoOkTwoFailTest {
    @Test
    void executeTwoOkTestMethodsAndTwoFailures() {
        TestScanResult scanInfo = scanPackages(getPackagePath(TwoOkTwoFail.class), Scanner::collectTestScanResult);
        TestRunResult result = SerialTester.runTests(scanInfo);

        assertTrue(findTestMethodResult(result, "ok1").getDurationMs() >= 10);
        assertEquals(4, TwoOkTwoFail.testCount);
        assertEquals(15, TwoOkTwoFail.counter);

        TestResultBuilder testResultBuilder = new TestResultBuilder();
        ClassResult classResult = testResultBuilder.addTestClassResult(TwoOkTwoFail.class, "ok1", "ok2");
        classResult.addTestResult("fail1", new ExceptionResult(new RuntimeException("should be caught")));
        classResult.addTestResult("fail2", new ExceptionResult(new AssertionFailedError("expected: <1> but was: <2>")));
        testResultBuilder.compareTestResults(result);
    }
}
