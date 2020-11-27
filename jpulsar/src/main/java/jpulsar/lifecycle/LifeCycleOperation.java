package jpulsar.lifecycle;

import java.util.function.Consumer;

public class LifeCycleOperation<R> {
    private Consumer<R> handler;
    private Runnable runner;

    public LifeCycleOperation(Consumer<R> handler) {
        this.handler = handler;
    }

    public LifeCycleOperation(Runnable runner) {
        this.runner = runner;
    }

    public Consumer<R> getHandler() {
        return handler;
    }

    public Runnable getRunner() {
        return runner;
    }
}
