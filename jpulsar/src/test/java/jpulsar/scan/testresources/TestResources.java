package jpulsar.scan.testresources;

import jpulsar.TestResource;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;
import jpulsar.scan.resources.TestResource3;

import java.util.function.Supplier;

public class TestResources {
    @TestResource
    public TestResource1 tr1() {
        return new TestResource1();
    }

    @TestResource
    public Supplier<TestResource2> tr2() {
        return TestResource2::new;
    }

    @TestResource
    public Supplier<Supplier<TestResource3>> tr3() {
        return () -> TestResource3::new;
    }
}
