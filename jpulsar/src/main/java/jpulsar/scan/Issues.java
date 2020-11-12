package jpulsar.scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Issues {
    private List<String> issues = new ArrayList<>();
    public List<String> getIssues() {
        return issues;
    }

    public void addIssue(String... s) {
        issues.add(Arrays.stream(s).collect(Collectors.joining(" ")));
    }

    public void addIssue(List<String> s) {
        issues.add(String.join(" ", s));
    }
}
