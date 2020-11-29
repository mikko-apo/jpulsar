package jpulsar.tester.resourcehandlers;

import jpulsar.ResourceHandler;
import jpulsar.TestResource;

public class GlobalSharedTestResources {
    @TestResource(shared = true)
    public ResourceHandler<TestResource2> tr2() {
        TestResource2 tr2 = new TestResource2();
        return new ResourceHandler<>(tr2)
                .beforeAll(() -> tr2.log("beforeAll-tr2"))
                .before(() -> tr2.log("before-tr2"))
                .after(() -> tr2.log("after-tr2"))
                .afterAll(() -> tr2.log("afterAll-tr2"));
    }
}
