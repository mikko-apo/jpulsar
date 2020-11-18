package jpulsar.tester;

import java.util.List;

public  class TestClassResult<T> {
    private final Class<T> clazz;
    private final List<TestMethodResult> testMethods;

    public TestClassResult(Class<T> clazz, List<TestMethodResult> testMethods) {
        this.clazz = clazz;
        this.testMethods = testMethods;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public List<TestMethodResult> getTestMethods() {
        return testMethods;
    }
}
