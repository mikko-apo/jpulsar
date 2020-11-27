package jpulsar.util;

public class Counter {
    private volatile int count;
    private final Integer max;

    public Counter(int count, Integer max) {
        this.count = count;
        this.max = max;
    }

    public Counter(int count) {
        this(count, null);
    }

    public Counter() {
        this(0);
    }

    public int add(int num) {
        count += num;
        if(max != null && count > max ) {
            count = count - max;
        }
        return count;
    }

    public int increment() {
        return add(1);
    }

    public int postfixIncrement() {
        int c = count;
        add(1);
        return c;
    }

    public int getCount() {
        return count;
    }
}
