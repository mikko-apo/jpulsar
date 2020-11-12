package jpulsar.scan.test_method;

import jpulsar.Test;
import jpulsar.TestResource;

public class TestMethods {
    @Test
    void test0() {

    }

    @Test
    void test1(TestMethodTestResource resource) {

    }

    @Test
    void testWithInvalidTestResource(TestMethodTestResource resource, TestMethodTestResource2 resource2) {

    }

    @TestResource
    TestMethodTestResource testResource() {
        return new TestMethodTestResource();
    }
}
