package jpulsar.tester.teststep;

import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.step.TestStep;
import jpulsar.tester.ExceptionResult;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStepLog {
    @Test
    void logTestSteps() {
        TestScanResult scanInfo = scanPackages(getPackagePath(TestStepTest.class), Scanner::collectTestClasses);
        TestRunResult result = SerialTester.runTests(scanInfo);
        TestRunResult expectedTestRunResult = new TestRunResult(asList(
                new TestClassResult<>(TestStepTest.class, asList(
                        new TestMethodResult("step",
                                null,
                                null,
                                asList(new TestStep("sleep1", null, null, null),
                                        new TestStep("sleep2", null, null, null))),
                        new TestMethodResult("stepFail",
                                new ExceptionResult(new RuntimeException("should be caught")),
                                null,
                                asList(new TestStep("sleep1", new ExceptionResult(new RuntimeException("should be caught")), null, null)))
                ))
        ));
        TestClassResult<?> testClassResult = result.testClassResults.get(0);
        assertTrue(testClassResult.getTestMethods().get(0).getDurationMs() >= 10);
        removeStacktraceDuration(result);
        removeStacktraceDuration(expectedTestRunResult);
        scannerJackson.jsonEquals(expectedTestRunResult, result);
    }
}
