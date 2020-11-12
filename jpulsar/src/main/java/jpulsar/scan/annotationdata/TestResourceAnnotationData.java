package jpulsar.scan.annotationdata;

import jpulsar.TestResourceScope;
import jpulsar.scan.Issues;

import java.util.List;

public class TestResourceAnnotationData extends Issues {
    public static int MaxDefault = 0;
    private final String name;
    private final Integer max;
    private final Boolean shared;
    private final Boolean fixed;
    private final Boolean hidden;
    private final TestResourceScope scope;
    private final List<String> usecases;

    public TestResourceAnnotationData(
            String name,
            Integer max,
            Boolean shared,
            Boolean fixed,
            Boolean hidden,
            TestResourceScope scope,
            List<String> usecases) {
        this.name = name != null && name.equals("") ? null : name;
        this.max = max != null && max != 0 ? max : null;
        this.shared = shared;
        this.fixed = fixed;
        this.hidden = hidden;
        this.scope = scope;
        this.usecases = usecases;
    }

    public String getName() {
        return name;
    }

    public Integer getMax() {
        return max;
    }

    public Boolean getShared() {
        return shared;
    }

    public Boolean getFixed() {
        return fixed;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public TestResourceScope getScope() {
        return scope;
    }

    public List<String> getUsecases() {
        return usecases;
    }
}
