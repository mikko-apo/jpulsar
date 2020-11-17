package jpulsar.util;

public class Benchmark {
    public volatile long start;

    public Benchmark() {
        start = System.currentTimeMillis();
    }

    public Benchmark(long start) {
        this.start = start;
    }

    public long durationMsAndSet() {
        long newtime = System.currentTimeMillis();
        long duration = newtime - start;
        start = newtime;
        return duration;
    }

    public long durationMs() {
        return System.currentTimeMillis() - start;
    }

    public Benchmark copy() {
        Benchmark benchmark = new Benchmark();
        benchmark.start = start;
        return benchmark;
    }
}
