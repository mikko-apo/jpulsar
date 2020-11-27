package jpulsar.tester.resourcehandlers;

import jpulsar.ResourceHandler;
import jpulsar.Test;
import jpulsar.TestResource;

public class ResourceHandlers {
    @TestResource
    public ResourceHandler<TestResource1> tr1() {
        return new ResourceHandler<>(new TestResource1())
                .beforeAll(tr1 -> tr1.log("beforeAll"))
                .before(tr1 -> tr1.log("before"))
                .after(tr1 -> tr1.log("after"))
                .afterAll(tr1 -> tr1.log("afterAll"));
    }

    @Test
    public void t1(TestResource1 tr1) {
        tr1.log("test");
    }
}
