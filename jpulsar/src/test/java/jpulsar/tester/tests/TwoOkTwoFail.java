package jpulsar.tester.tests;

import jpulsar.Test;

import static jpulsar.util.Threads.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TwoOkTwoFail {
    static int counter = 0;

    @Test
    public void ok1() {
        counter++;
        assertEquals(1, counter);
        sleep(10);
    }

    @Test
    public void ok2() {
        counter += 2;
        assertEquals(3, counter);
    }

    @Test
    public void fail1() {
        counter += 4;
        throw new RuntimeException("should be caught");
    }

    @Test
    public void fail2() {
        assertEquals(1, 2);
    }
}