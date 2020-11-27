package jpulsar.tester;

import jpulsar.step.TestStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
import static jpulsar.util.Collections.map;
import static jpulsar.util.Util.scannerJackson;

public class TestResultBuilder {
    private final List<TestClassResult<?>> testClassResults = new ArrayList<>();
    private final TestRunResult testRunResult = new TestRunResult(testClassResults);

    public <T> ClassResult addTestClassResult(Class<T> aClass, String... testMethodResultNames) {
        TestClassResult<T> testClassResult = new TestClassResult<>(aClass, new ArrayList<>(map(testMethodResultNames,
                s -> new TestMethodResult(s, null, null, asList()))));
        testRunResult.testClassResults.add(testClassResult);
        return new ClassResult(testClassResult);
    }

    public void compareTestResults(TestRunResult result) {
        removeStacktraceDuration(testRunResult);
        removeStacktraceDuration(result);
        scannerJackson.jsonEquals(this.testRunResult, result);
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
}