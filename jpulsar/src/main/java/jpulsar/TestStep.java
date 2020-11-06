package jpulsar;

import java.util.function.Supplier;

public class TestStep {
    /**
     * Logs test execution time and that this step was reached
     */
    public static void testStep(String name) {

    }

    /**
     * Logs test execution time and that this step was reached and if testStep code failed
     */
    public static void testStep(String name, Runnable testBlock) {
        testBlock.run();
    }

    /**
     * Logs test execution time and that this step was reached and if testStep code failed
     */
    public static <T> T testStep(String name, Supplier<T> testBlock) {
        return testBlock.get();
    }
}
