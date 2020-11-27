package jpulsar.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class TestLifecycleOperations<R, RB> {
    private ArrayList<LifeCycleOperation<?>> beforeAlls = new ArrayList<>();
    private ArrayList<LifeCycleOperation<?>> befores = new ArrayList<>();
    private ArrayList<LifeCycleOperation<?>> afters = new ArrayList<>();
    private ArrayList<LifeCycleOperation<?>> afterAlls = new ArrayList<>();

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

    public List<LifeCycleOperation<?>> getBeforeAlls() {
        return beforeAlls;
    }

    public List<LifeCycleOperation<?>> getBefores() {
        return befores;
    }

    public List<LifeCycleOperation<?>> getAfters() {
        return afters;
    }

    public List<LifeCycleOperation<?>> getAfterAlls() {
        return afterAlls;
    }
}
