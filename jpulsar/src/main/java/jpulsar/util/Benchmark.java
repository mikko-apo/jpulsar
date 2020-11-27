package jpulsar.util;

public class Benchmark {
    public volatile long start;

    public Benchmark() {
        start = System.currentTimeMillis();
    }

    public Benchmark(long start) {
        this.start = start;
    }

    public int durationMsAndSet() {
        long newtime = System.currentTimeMillis();
        long duration = newtime - start;
        start = newtime;
        return (int) duration;
    }

    public int durationMs() {
        return (int) (System.currentTimeMillis() - start);
    }
}
