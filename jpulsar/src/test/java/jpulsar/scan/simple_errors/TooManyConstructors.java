package jpulsar.scan.simple_errors;

import jpulsar.Test;

public class TooManyConstructors {
    public TooManyConstructors() {
    }

    public TooManyConstructors(String s) {
    }

    @Test
    void test() {

    }
}
