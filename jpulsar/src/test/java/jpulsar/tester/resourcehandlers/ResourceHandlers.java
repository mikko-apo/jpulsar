package jpulsar.tester.resourcehandlers;

import jpulsar.ResourceHandler;
import jpulsar.Test;
import jpulsar.TestResource;

public class ResourceHandlers {
    @TestResource
    public ResourceHandler<TestResource1> tr1() {
        return new ResourceHandler<>(new TestResource1())
                .beforeAll(tr1 -> tr1.log("beforeAll-tr1"))
                .before(tr1 -> tr1.log("before-tr1"))
                .after(tr1 -> tr1.log("after-tr1"))
                .afterAll(tr1 -> tr1.log("afterAll-tr1"));
    }

    @TestResource(shared = true)
    public ResourceHandler<TestResource3> tr3() {
        return new ResourceHandler<>(new TestResource3())
                .beforeAll(tr -> tr.log("beforeAll-tr3"))
                .before(tr -> tr.log("before-tr3"))
                .after(tr -> tr.log("after-tr3"))
                .afterAll(tr -> tr.log("afterAll-tr3"));
    }

    @Test
    public void test1(TestResource1 tr1, TestResource2 tr2, TestResource3 tr3) {
        tr1.log("test1-tr1");
        tr2.log("test1-tr2");
        tr3.log("test1-tr3");
    }

    @Test
    public void test2(TestResource1 tr1, TestResource2 tr2, TestResource3 tr3) {
        tr1.log("test2-tr1");
        tr2.log("test2-tr2");
        tr3.log("test2-tr3");
    }
}
