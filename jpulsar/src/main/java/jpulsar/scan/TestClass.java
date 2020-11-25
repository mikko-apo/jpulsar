package jpulsar.scan;

import jpulsar.ResourceHandler;
import jpulsar.scan.method.ConstructorInfo;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;

import java.util.ArrayList;
import java.util.List;

public class TestClass<T> extends Issues {
    final private Class<T> clazz;
    final private List<TestMethod> testMethods;
    final private List<TestResourceMethod> testResources;
    List<ResourceHandler<?>> resourceHandlers = new ArrayList<>();
    private ConstructorInfo constructor;

    public TestClass(Class<T> clazz) {
        this(clazz, null, new ArrayList<>(), new ArrayList<>());
    }

    public TestClass(Class<T> clazz, ConstructorInfo constructor, List<TestMethod> testMethods, List<TestResourceMethod> testResources) {
        this.clazz = clazz;
        this.constructor = constructor;
        this.testMethods = testMethods;
        this.testResources = testResources;
    }

    public ConstructorInfo getConstructor() {
        return constructor;
    }

    public TestClass<T> setConstructor(ConstructorInfo constructor) {
        this.constructor = constructor;
        return this;
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
