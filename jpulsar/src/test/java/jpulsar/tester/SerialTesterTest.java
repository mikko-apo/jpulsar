package jpulsar.tester;


import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.step.TestStep;
import jpulsar.tester.tests.TwoOkTwoFail;
import jpulsar.tester.teststep.TestStepTest;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.scan.ScannerTest.getPackagePath;
import static jpulsar.util.Util.scannerJackson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerialTesterTest {

    @Test
    void executeTwoOkTestMethodsAndTwoFailures() {
        TestScanResult scanInfo = scanPackages(getPackagePath(TwoOkTwoFail.class), Scanner::collectTestClasses);
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

    TestMethodResult findTestMethodResult(TestRunResult result, String name) {
        List<TestMethodResult> list = new ArrayList<>();
        for (TestClassResult<?> testClassResult : result.testClassResults) {
            for(TestMethodResult testMethodResult : testClassResult.getTestMethods()) {
                if(testMethodResult.getName().equals(name)) {
                    list.add(testMethodResult);
                }
            }
        }
        if(list.size() != 1) {
            throw new RuntimeException("Found " + list.size() + " test method results with name: " + name);
        }
        return list.get(0);
    }

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

    private void removeStacktraceDuration(TestRunResult result) {
        result.testClassResults.sort(Comparator.comparing(testClassResult -> testClassResult.getClazz().getName()));
        for (TestClassResult<?> testClassResult : result.testClassResults) {
            testClassResult.getTestMethods().sort(Comparator.comparing(TestMethodResult::getName));
            for (TestMethodResult testMethodResult : testClassResult.getTestMethods()) {
                testMethodResult.setDurationMs(null);
                if (testMethodResult.getException() != null) {
                    testMethodResult.getException().setStackTrace(null);
                }
                for (TestStep testStep : testMethodResult.getSteps()) {
                    testStep.setDurationMs(null);
                    testStep.setStartMs(null);
                    if (testStep.getException() != null) {
                        testStep.getException().setStackTrace(null);
                    }
                }
            }
        }
    }
}
