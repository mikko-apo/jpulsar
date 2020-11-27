package jpulsar.tester.teststep;

import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.tester.ClassResult;
import jpulsar.tester.ExceptionResult;
import jpulsar.tester.SerialTester;
import jpulsar.tester.TestResultBuilder;
import jpulsar.tester.TestRunResult;
import org.junit.jupiter.api.Test;

import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.tester.TestResultBuilder.findTestMethodResult;
import static jpulsar.util.Util.getPackagePath;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStepLogTest {
    @Test
    void logTestSteps() {
        TestScanResult scanInfo = scanPackages(getPackagePath(TestSteps.class), Scanner::collectTestScanResult);
        TestRunResult result = SerialTester.runTests(scanInfo);

        assertTrue(findTestMethodResult(result, "step").getDurationMs() >= 10);

        TestResultBuilder testResultBuilder = new TestResultBuilder();
        ClassResult classResult = testResultBuilder.addTestClassResult(TestSteps.class);
        classResult.addTestResult("step")
                .testStep("sleep1")
                .testStep("sleep2");
        classResult.addTestResult("stepFail", new ExceptionResult(new RuntimeException("should be caught")))
                .testStep("sleep1", new ExceptionResult(new RuntimeException("should be caught")));
        testResultBuilder.compareTestResults(result);
    }
}
