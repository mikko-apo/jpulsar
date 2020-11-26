package jpulsar.tester.testresources;

import jpulsar.Test;
import jpulsar.TestResource;

public class TestResources {
    @TestResource
    public static TestResource1 tr1() {
        return new TestResource1();
    }

    @TestResource
    public TestResource2 tr2() {
        return new TestResource2();
    }

    @Test
    public void testTwoTestResources(TestResource1 tr1, TestResource2 tr2) {
        tr1.add();
        tr2.add();
    }
}
