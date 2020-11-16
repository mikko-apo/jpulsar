package jpulsar.scan;

import jpulsar.ResourceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestScanResult {
    List<TestClass<?>> testClasses = new ArrayList<>();
    List<ResourceHandler<?>> globalResourceHandlers = new ArrayList<>();

    public List<String> getIssues() {
        Stream<String> stream = testClasses.stream().flatMap(testClass -> testClass.getIssues().stream());
        return stream.collect(Collectors.toList());
    }

    public <T> void addTestClass(TestClass<T> testClass) {
        testClasses.add(testClass);
    }
}
