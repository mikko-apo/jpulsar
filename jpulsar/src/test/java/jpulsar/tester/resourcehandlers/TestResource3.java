package jpulsar.tester.resourcehandlers;

import java.util.ArrayList;
import java.util.List;

public class TestResource3 {
    public static int count = 0;
    public static List<String> log = new ArrayList<>();

    public TestResource3() {
        count++;
    }

    public void log(String s) {
        log.add(s);
    }
}
