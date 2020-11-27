package jpulsar.scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jpulsar.util.Collections.nonNull;
import static jpulsar.util.Strings.joinSpaced;

public class Issues {
    private List<String> issues = new ArrayList<>();

    public List<String> getIssues() {
        return issues;
    }

    public void addIssue(String s) {
        addIssue(Arrays.asList(s));
    }

    public void addIssue(List<Object> s) {
        issues.add(joinSpaced(nonNull(s)));
    }
}
