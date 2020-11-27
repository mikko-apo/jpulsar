package jpulsar.tester;

import jpulsar.step.TestStep;

import java.util.List;

import static java.util.Arrays.asList;

public class ClassResult {
    private TestClassResult<?> testClassResult;

    public ClassResult(TestClassResult<?> testClassResult) {
        this.testClassResult = testClassResult;
    }

    public MethodResult addTestResult(String name) {
        return addTestResult(name, asList());
    }

    public MethodResult addTestResult(String name, ExceptionResult exceptionResult) {
        return addTestResult(name, exceptionResult, asList());
    }

    public MethodResult addTestResult(String name, List<TestStep> steps) {
        return addTestResult(name, null, steps);
    }

    public MethodResult addTestResult(String name, ExceptionResult exceptionResult, List<TestStep> steps) {
        TestMethodResult testMethodResult = new TestMethodResult(name, exceptionResult, null, steps);
        testClassResult.getTestMethods().add(testMethodResult);
        return new MethodResult(testMethodResult);
    }
}