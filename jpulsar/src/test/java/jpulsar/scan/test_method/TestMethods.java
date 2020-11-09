package jpulsar.scan.test_method;

import jpulsar.Test;
import jpulsar.TestResource;

public class TestMethods {
    @Test
    void test() {

    }

    @Test
    void test(TestMethodTestResource resource) {

    }

    @Test
    void testWithInvalidTestResource(TestMethodTestResource resource, TestMethodTestResource2 resource2) {

    }

    @TestResource
    TestMethodTestResource testResource() {
        return new TestMethodTestResource();
    }
}
