package jpulsar.scan;

import io.github.classgraph.MethodInfo;
import jpulsar.ResourceHandler;
import jpulsar.TestResourceScope;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jpulsar.util.Streams.filter;

public class TestClass<T> extends Issues {
    final private Class<T> clazz;
    final private List<TestMethod> testMethods;
    final private List<TestResourceMethod> testResources;
    List<ResourceHandler<?>> resourceHandlers = new ArrayList<>();
    private ConstructorInfo constructorInfo;

    public TestClass(Class<T> clazz) {
        this(clazz, null, new ArrayList<>(), new ArrayList<>());
    }

    public TestClass(Class<T> clazz, ConstructorInfo constructorInfo, List<TestMethod> testMethods, List<TestResourceMethod> testResources) {
        this.clazz = clazz;
        this.constructorInfo = constructorInfo;
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

    public ConstructorInfo getConstructorInfo() {
        return constructorInfo;
    }

    public void setConstructorInfo(ConstructorInfo constructorInfo) {
        this.constructorInfo = constructorInfo;
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

    public List<TestResourceMethod> getTestResourceMethod(TestResourceScope scope) {
        return filter(testResources,
                testResourceMethod -> testResourceMethod.getTestResourceAnnotation().getScope() == scope);
    }
}
