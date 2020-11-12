package jpulsar.scan.annotationdata;

import java.util.List;

public class TestAnnotationData {
    private final String name;
    private final List<String> usecases;
    private final List<String> tags;

    public TestAnnotationData(String name, List<String> usecases, List<String> tags) {
        this.name = name != null && name.equals("") ? null : name;
        this.usecases = usecases;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public List<String> getUsecases() {
        return usecases;
    }

    public List<String> getTags() {
        return tags;
    }
}
