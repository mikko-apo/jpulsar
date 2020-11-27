package jpulsar;

import jpulsar.lifecycle.TestLifecycleOperations;

public class ResourceHandler<R> extends TestLifecycleOperations<R, ResourceHandler<R>> {
    private final R resource;

    public ResourceHandler(R resource) {
        this.resource = resource;
    }

    public ResourceHandler<R> getThis() {
        return this;
    }

    public R getResource() {
        return resource;
    }
}
