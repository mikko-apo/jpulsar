package jpulsar.scan.simple_errors;

import jpulsar.Test;
import jpulsar.TestResource;

public class BothTestAndTestResource {
    @Test
    @TestResource
    void test() {

    }
}
