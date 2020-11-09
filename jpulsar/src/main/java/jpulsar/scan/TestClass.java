package jpulsar.scan;

import io.github.classgraph.MethodInfo;
import jpulsar.ResourceHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestClass<T> {
    List<ResourceHandler<?>> resourceHandlers = new ArrayList<>();
    final private Class<T> clazz;
    final private List<TestMethod> testMethods = new ArrayList<>();
    private List<String> issues = new ArrayList<>();

    public TestClass(Class<T> clazz) {
        this.clazz = clazz;
    }


    public void addIssue(String... s) {
        issues.add(Arrays.stream(s).collect(Collectors.joining(" ")));
    }

    public void addIssue(List<String> s) {
        issues.add(String.join(" ", s));
    }

    public void addClassIssue(String... s) {
        List<String> list = new ArrayList<>();
        list.add(clazz.getName());
        list.addAll(Arrays.asList(s));
        addIssue(list);
    }

    public void addMethodIssue(MethodInfo method, String s) {
        List<String> list = new ArrayList<>();
        list.add(clazz.getName() + "." + method.getName() + "()");
        list.addAll(Arrays.asList(s));
        addIssue(list);
    }

    public List<String> getIssues() {
        return issues;
    }

    public void addTestMethod(TestMethod testMethod) {
        testMethods.add(testMethod);
    }

    public void addTestResource(MethodInfo methodInfo) {

    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }
}
