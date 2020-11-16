package jpulsar.lifecycle;

import java.util.ArrayList;
import java.util.function.Consumer;

class LifeCycleOperation<R> {
    Consumer<R> handler;
    Runnable runner;

    public LifeCycleOperation(Consumer<R> handler) {
        this.handler = handler;
    }

    public LifeCycleOperation(Runnable runner) {
        this.runner = runner;
    }
}

public abstract class TestLifecycleOperations<R, RB> {
    ArrayList<LifeCycleOperation<?>> beforeAlls = new ArrayList<>();
    ArrayList<LifeCycleOperation<?>> befores = new ArrayList<>();
    ArrayList<LifeCycleOperation<?>> afters = new ArrayList<>();
    ArrayList<LifeCycleOperation<?>> afterAlls = new ArrayList<>();

    abstract public RB getThis();

    public RB beforeAll(Consumer<R> handler) {
        beforeAlls.add(new LifeCycleOperation<>(handler));
        return getThis();
    }

    public RB beforeAll(Runnable runner) {
        beforeAlls.add(new LifeCycleOperation<>(runner));
        return getThis();
    }

    public RB before(Consumer<R> handler) {
        befores.add(new LifeCycleOperation<>(handler));
        return getThis();
    }

    public RB before(Runnable runner) {
        befores.add(new LifeCycleOperation<>(runner));
        return getThis();
    }

    public RB after(Consumer<R> handler) {
        afters.add(new LifeCycleOperation<>(handler));
        return getThis();
    }

    public RB after(Runnable runner) {
        afters.add(new LifeCycleOperation<>(runner));
        return getThis();
    }

    public RB afterAll(Consumer<R> handler) {
        afterAlls.add(new LifeCycleOperation<>(handler));
        return getThis();
    }

    public RB afterAll(Runnable runner) {
        afterAlls.add(new LifeCycleOperation<>(runner));
        return getThis();
    }
}
