package jpulsar.tester.teststep;

import jpulsar.Test;

import static jpulsar.step.TestStepCollector.testStep;
import static jpulsar.util.Threads.sleep;

public class TestStepTest {
    @Test
    public void step() {
        sleep(10);
        testStep("sleep1");
        sleep(10);
        testStep("sleep2");
    }
    @Test
    public void stepFail() {
        sleep(5);
        testStep("sleep1", () -> {
            sleep(10);
            throw new RuntimeException("should be caught");
        });
    }
}
