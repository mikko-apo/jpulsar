package jpulsar.step;

import jpulsar.tester.ExceptionResult;

public class TestStep {
    private final String name;
    private final ExceptionResult exception;
    private Long startMs;
    private Long durationMs;

    public TestStep(String name, ExceptionResult exception, Long startMs, Long durationMs) {
        this.name = name;
        this.exception = exception;
        this.startMs = startMs;
        this.durationMs = durationMs;
    }

    public String getName() {
        return name;
    }

    public ExceptionResult getException() {
        return exception;
    }

    public Long getStartMs() {
        return startMs;
    }

    public void setStartMs(Long startMs) {
        this.startMs = startMs;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}
