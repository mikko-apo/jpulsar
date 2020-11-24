package jpulsar.tester;

import jpulsar.step.TestStep;

import java.util.List;

public class TestMethodResult {
    private final String name;
    private final ExceptionResult exception;
    private Integer durationMs;
    private final List<TestStep> steps;

    public TestMethodResult(String name, ExceptionResult exception, Integer durationMs, List<TestStep> steps) {
        this.name = name;
        this.exception = exception;
        this.durationMs = durationMs;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public ExceptionResult getException() {
        return exception;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

    public List<TestStep> getSteps() {
        return steps;
    }
}
