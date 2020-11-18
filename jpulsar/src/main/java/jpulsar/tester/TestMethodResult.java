package jpulsar.tester;

import jpulsar.step.TestStep;

import java.util.List;

public class TestMethodResult {
    private final String name;
    private final ExceptionResult exception;
    private Long durationMs;
    private final List<TestStep> steps;

    public TestMethodResult(String name, ExceptionResult exception, Long durationMs, List<TestStep> steps) {
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

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public List<TestStep> getSteps() {
        return steps;
    }
}
