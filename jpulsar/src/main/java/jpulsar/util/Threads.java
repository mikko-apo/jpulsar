package jpulsar.util;

public class Threads {
    static public void sleep(long ms) {
        try {
            if(ms > 0) {
                Thread.sleep(ms);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
