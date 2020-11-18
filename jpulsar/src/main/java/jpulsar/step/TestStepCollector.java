package jpulsar.step;

import jpulsar.tester.ExceptionResult;
import jpulsar.util.Benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TestStepCollector {
    public static ThreadLocal<TestStepCollector> threadLocalCollector = new ThreadLocal<>();
    private final Benchmark benchmark;
    private final List<TestStep> steps = new ArrayList<>();

    public TestStepCollector(long start) {
        threadLocalCollector.set(this);
        this.benchmark = new Benchmark(start);
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    /**
     * Logs test execution time and that this step was reached
     */
    public static void testStep(String name) {
        threadLocalCollector.get().add(name);
    }

    private void add(String name) {
        steps.add(new TestStep(name, null, benchmark.durationMs(), null));
    }

    /**
     * Logs test execution time and that this step was reached and if testStep code failed
     */
    public static void testStep(String name, Runnable testBlock) {
        testStep(name, () -> {
            testBlock.run();
            return 1;
        });
    }

    /**
     * Logs test execution time and that this step was reached and if testStep code failed
     */
    public static <T> T testStep(String name, Supplier<T> testBlock) {
        return threadLocalCollector.get().add(name, testBlock);
    }

    private <T> T add(String name, Supplier<T> testBlock) {
        Throwable throwable = null;
        long startMs = benchmark.durationMs();
        try {
            return testBlock.get();
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            ExceptionResult exceptionResult = null;
            if (throwable != null) {
                exceptionResult = new ExceptionResult(throwable);
            }
            steps.add(new TestStep(name, exceptionResult, startMs, benchmark.durationMs() - startMs));
        }
    }


}
