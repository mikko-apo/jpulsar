package jpulsar.scan;

import io.github.classgraph.MethodInfo;
import jpulsar.ResourceHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestClass<T> extends Issues {
    List<ResourceHandler<?>> resourceHandlers = new ArrayList<>();
    final private Class<T> clazz;
    final private List<TestMethod> testMethods;
    final private List<TestResourceMethod> testResources;

    public TestClass(Class<T> clazz) {
        this(clazz, new ArrayList<>(), new ArrayList<>());
    }

    public TestClass(Class<T> clazz, List<TestMethod> testMethods, List<TestResourceMethod> testResources) {
        this.clazz = clazz;
        this.testMethods = testMethods;
        this.testResources = testResources;
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

    public void addTestMethod(TestMethod testMethod) {
        testMethods.add(testMethod);
    }

    public void addTestResource(TestResourceMethod methodInfo) {
        testResources.add(methodInfo);
    }

    public List<TestMethod> getTestMethods() {
        return testMethods;
    }

    public List<TestResourceMethod> getTestResources() {
        return testResources;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
