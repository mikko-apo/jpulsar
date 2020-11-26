package jpulsar.scan.testresources;

import jpulsar.ResourceHandler;
import jpulsar.Test;
import jpulsar.TestResource;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;
import jpulsar.scan.resources.TestResource3;
import jpulsar.scan.resources.TestResource4;
import jpulsar.scan.resources.TestResource5;
import jpulsar.scan.resources.TestResource6;

import java.util.function.Supplier;

public class TestResources {
    @TestResource
    TestResource1 tr1() {
        return new TestResource1();
    }

    @TestResource
    Supplier<TestResource2> tr2() {
        return TestResource2::new;
    }

    @TestResource
    Supplier<Supplier<TestResource3>> tr3() {
        return () -> TestResource3::new;
    }

    @TestResource
    ResourceHandler<TestResource4> testResourceWithResourceHandler() {
        return new ResourceHandler<>(new TestResource4());
    }

    @TestResource
    TestResource5 testResourceWithDependency(TestResource1 tr) {
        return new TestResource5();
    }

    @TestResource
    TestResource6 testResourceWithoutTestsUsingIt() {
        return new TestResource6();
    }

    @Test
    void test0() {

    }

    @Test
    void test1(TestResource1 resource) {

    }

    @Test
    void testWithInvalidTestResource(TestResource1 resource, String resource2) {

    }

    @Test
    void testWithResourceHandler(TestResource4 resource) {

    }

    @Test
    void testWithTestResourceWithDependency(TestResource5 resource) {

    }

    @Test
    void testWithSameTestResourceTwice(TestResource1 resourceA, TestResource1 resourceB) {

    }

    @Test
    void testWithSupplierDependency(Supplier<TestResource1> trSupplier) {

    }
}
