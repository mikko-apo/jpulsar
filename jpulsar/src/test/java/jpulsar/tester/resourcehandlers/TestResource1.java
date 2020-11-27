package jpulsar.tester.resourcehandlers;

import java.util.ArrayList;
import java.util.List;

public class TestResource1 {
    public static List<String> log = new ArrayList<>();

    public void log(String s) {
        log.add(s);
    }
}
