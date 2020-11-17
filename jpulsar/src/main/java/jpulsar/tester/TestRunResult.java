package jpulsar.tester;

import java.util.List;

public class TestRunResult {
    public final List<TestClassResult> testClassResults;

    public TestRunResult(List<TestClassResult> testClassResults) {
        this.testClassResults = testClassResults;
    }
}
