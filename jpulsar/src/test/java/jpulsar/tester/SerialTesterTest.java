package jpulsar.tester;


import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.tester.tests.TwoOkTwoFail;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static java.util.Arrays.asList;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.scan.ScannerTest.getPackagePath;
import static jpulsar.util.Util.jsonEquals;

public class SerialTesterTest {

    @Test
    void executeTwoOkTestMethodsAndTwoFailures() {
        TestScanResult scanInfo = scanPackages(getPackagePath(TwoOkTwoFail.class), Scanner::collectTestClasses);
        TestRunResult result = SerialTester.runTests(scanInfo);
        TestRunResult expectedTestRunResult = new TestRunResult(asList(
                new TestClassResult<>(TwoOkTwoFail.class, asList(
                        new TestMethodResult("ok1", null),
                        new TestMethodResult("ok2", null),
                        new TestMethodResult("fail1", new ExceptionResult(new RuntimeException("should be caught"))),
                        new TestMethodResult("fail2", new ExceptionResult(new AssertionFailedError("expected: <1> but was: <2>")))
                ))
        ));
        removeStacktrace(result);
        removeStacktrace(expectedTestRunResult);
        jsonEquals(expectedTestRunResult, result);
    }

    private void removeStacktrace(TestRunResult result) {
        for (TestClassResult<?> testClassResult : result.testClassResults) {
           for(TestMethodResult testMethodResult : testClassResult.getTestMethods()) {
               if(testMethodResult.getException() != null) {
                   testMethodResult.getException().setStackTrace(null);
               }
           }
        }
    }
}
