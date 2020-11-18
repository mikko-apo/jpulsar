package jpulsar.tester;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionResult {
    private final Class<? extends Throwable> clazz;
    private final String message;
    private String stackTrace;

    public ExceptionResult(Throwable e) {
        this.clazz = e.getClass();
        this.message = e.getMessage();
        this.stackTrace = stacktraceToString(e);
    }

    public Class<? extends Throwable> getClazz() {
        return clazz;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    private static String stacktraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
