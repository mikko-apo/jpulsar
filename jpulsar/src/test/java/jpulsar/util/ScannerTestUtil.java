package jpulsar.util;

import jpulsar.TestResourceScope;
import jpulsar.scan.TestClassBuilder;
import jpulsar.step.TestStep;
import jpulsar.tester.TestClassResult;
import jpulsar.tester.TestMethodResult;
import jpulsar.tester.TestRunResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScannerTestUtil {
    public static TestMethodResult findTestMethodResult(TestRunResult result, String name) {
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

    public static void removeStacktraceDuration(TestRunResult result) {
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

    public static final jpulsar.Test emptyTestAnnotation = TestClassBuilder.createTestAnnotation("", new String[0], new String[0]);
    public static final jpulsar.TestResource emptyTestResourceAnnotation = TestClassBuilder.createTestResourceAnnotation("",
            0,
            false,
            false,
            false,
            TestResourceScope.GLOBAL,
            new String[0]
    );
    public static final jpulsar.TestResource emptyClassTestResourceAnnotation = TestClassBuilder.createTestResourceAnnotation("",
            0,
            false,
            false,
            false,
            TestResourceScope.CLASS,
            new String[0]
    );
}
