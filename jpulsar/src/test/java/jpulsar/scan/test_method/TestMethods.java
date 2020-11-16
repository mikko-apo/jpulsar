package jpulsar.scan.test_method;

import jpulsar.Test;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;

public class TestMethods {
    @Test
    void test0() {

    }

    @Test
    void test1(TestResource1 resource) {

    }

    @Test
    void testWithInvalidTestResource(TestResource1 resource, TestResource2 resource2) {

    }

    @jpulsar.TestResource
    TestResource1 testResource() {
        return new TestResource1();
    }
}
