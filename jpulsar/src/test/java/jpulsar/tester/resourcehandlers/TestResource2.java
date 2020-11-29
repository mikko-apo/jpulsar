package jpulsar.tester.resourcehandlers;

import java.util.ArrayList;
import java.util.List;

public class TestResource2 {
    public static int count = 0;
    public static List<String> log = new ArrayList<>();

    public TestResource2() {
        count++;
    }

    public void log(String s) {
        log.add(s);
    }
}
