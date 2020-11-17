package jpulsar.tester;

public class TestMethodResult {
    private final String name;
    private final ExceptionResult exception;

    public TestMethodResult(String name, ExceptionResult exception) {
        this.name = name;
        this.exception = exception;
    }

    public String getName() {
        return name;
    }

    public ExceptionResult getException() {
        return exception;
    }
}
