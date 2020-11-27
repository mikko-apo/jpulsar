package jpulsar.tester;

import jpulsar.step.TestStep;

public class MethodResult {
    private final TestMethodResult testMethodResult;

    public MethodResult(TestMethodResult testMethodResult) {
        this.testMethodResult = testMethodResult;
    }

    public MethodResult testStep(String name) {
        return testStep(name, null);
    }

    public MethodResult testStep(String name, ExceptionResult exception) {
        TestStep testStep = new TestStep(name, exception, null, null);
        testMethodResult.getSteps().add(testStep);
        return this;
    }
}
