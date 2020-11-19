package jpulsar.tester.tests;

import jpulsar.Test;

import static jpulsar.util.Threads.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TwoOkTwoFail {
    public static int testCount;
    static int counter = 0;

    @Test
    public void ok1() {
        counter++;
        testCount++;
        assertEquals(1, counter);
        sleep(10);
    }

    @Test
    public void ok2() {
        testCount++;
        counter += 2;
        assertEquals(3, counter);
    }

    @Test
    public void fail1() {
        testCount++;
        counter += 4;
        throw new RuntimeException("should be caught");
    }

    @Test
    public void fail2() {
        testCount++;
        assertEquals(1, 2);
    }
}
