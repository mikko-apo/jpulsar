package jpulsar.step;

import jpulsar.tester.ExceptionResult;

public class TestStep {
    private final String name;
    private final ExceptionResult exception;
    private Integer startMs;
    private Integer durationMs;

    public TestStep(String name, ExceptionResult exception, Integer startMs, Integer durationMs) {
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

    public Integer getStartMs() {
        return startMs;
    }

    public void setStartMs(Integer startMs) {
        this.startMs = startMs;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }
}
