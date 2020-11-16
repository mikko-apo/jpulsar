package jpulsar.scan;

import java.util.ArrayList;
import java.util.List;

import static jpulsar.util.Strings.join;

public class Issues {
    private List<String> issues = new ArrayList<>();

    public List<String> getIssues() {
        return issues;
    }

    public void addIssue(String... s) {
        issues.add(join(s, " "));
    }

    public void addIssue(List<String> s) {
        issues.add(String.join(" ", s));
    }
}
