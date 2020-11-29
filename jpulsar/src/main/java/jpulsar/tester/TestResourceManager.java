package jpulsar.tester;

import jpulsar.ResourceHandler;
import jpulsar.TestResourceScope;
import jpulsar.lifecycle.LifeCycleOperation;
import jpulsar.lifecycle.TestLifecycleOperations;
import jpulsar.scan.method.TestResourceMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static jpulsar.util.Collections.toList;

public class TestResourceManager implements AutoCloseable {
    private Map<TestResourceMethod, Object> globalSharedTestResources = new HashMap<>();
    private Map<TestResourceMethod, Object> classSharedTestResources = new HashMap<>();

    public void withTestResourceParameters(List<TestResourceMethod> parameterTestResources, Consumer<Object[]> f) {
        MethodParameters methodParameters = resolveParametersFromTestResourceMethods(parameterTestResources);
        executeLifecycleOps(methodParameters.getResourceHandlers(), TestLifecycleOperations::getBefores);
        f.accept(methodParameters.parameterArray());
        executeLifecycleOps(methodParameters.getResourceHandlers(), TestLifecycleOperations::getAfters);
        executeLifecycleOps(methodParameters.getPrivateResourceHandlersAfterAllCalledRightAfterTest(), TestLifecycleOperations::getAfterAlls);
    }

    public void cleanClassResourceHandlers() {
        executeLifecycleOpsForTestResources(classSharedTestResources.values(), TestLifecycleOperations::getAfterAlls);
        classSharedTestResources.clear();
    }

    private MethodParameters resolveParametersFromTestResourceMethods(List<TestResourceMethod> testResourceMethods) {
        MethodParameters methodParameters = new MethodParameters();
        for (TestResourceMethod testResourceMethod : testResourceMethods) {
            Object param = resolveParameter(testResourceMethod);
            methodParameters.addParameter(param, testResourceMethod.getTestResourceAnnotation().shared());
        }
        return methodParameters;
    }

    private Object resolveParameter(TestResourceMethod testResourceMethod) {
        // if a value exists for @TestResourceMethod in global or class level cache, use the cached version
        if (classSharedTestResources.containsKey(testResourceMethod)) {
            return classSharedTestResources.get(testResourceMethod);
        } else if (globalSharedTestResources.containsKey(testResourceMethod)) {
            return globalSharedTestResources.get(testResourceMethod);
        } else {
            try {
                Object param = initializeAndCallTestResourceMethod(testResourceMethod);
                // when parameter is ResourceHandler, call just initialized ResourceHandler's all beforeAll methods
                if (param instanceof ResourceHandler<?>) {
                    ResourceHandler<?> resourceHandler = (ResourceHandler<?>) param;
                    executeLifecycleOps(asList(resourceHandler), TestLifecycleOperations::getBeforeAlls);
                }
                // if @TestResourceMethod.shared == true save param to global or class level cache
                if (testResourceMethod.getTestResourceAnnotation().shared()) {
                    if (testResourceMethod.scope() == TestResourceScope.GLOBAL) {
                        globalSharedTestResources.put(testResourceMethod, param);
                    } else {
                        classSharedTestResources.put(testResourceMethod, param);
                    }
                }
                return param;
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Object initializeAndCallTestResourceMethod(TestResourceMethod testResourceMethod) throws
            InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Method m = testResourceMethod.getMethod();
        // TODO: Needs to resolve @TestResource's @TestResource parameters recursively
        if (Modifier.isStatic(m.getModifiers())) {
            return m.invoke(null);
        } else {
            // TODO: Needs to resolve @TestResource's getDeclaringClass' constructor @TestResource parameters recursively
            Constructor<?> constructor = m.getDeclaringClass().getConstructor();
            Object instance = constructor.newInstance();
            return m.invoke(instance);
        }
    }

    private static void executeLifecycleOpsForTestResources(Collection<Object> testResources,
                                                            Function<ResourceHandler<?>, Collection<LifeCycleOperation<?>>> opListSupplier) {
        List<ResourceHandler<?>> resourceHandlers = toList(testResources.stream()
                .filter(o -> o instanceof ResourceHandler)
                .map(o -> (ResourceHandler<?>) o));
        executeLifecycleOps(resourceHandlers, opListSupplier);
    }

    private static void executeLifecycleOps(Collection<ResourceHandler<?>> resourceHandlers,
                                            Function<ResourceHandler<?>, Collection<LifeCycleOperation<?>>> opListSupplier) {
        for (ResourceHandler<?> resourceHandler : resourceHandlers) {
            Object resource = resourceHandler.getResource();
            for (LifeCycleOperation<?> op : opListSupplier.apply(resourceHandler)) {
                executeLifecycleOp(resource, op);
            }
        }
    }

    private static <T> void executeLifecycleOp(Object resource, LifeCycleOperation<T> op) {
        Consumer<T> handler = op.getHandler();
        if (handler != null) {
            handler.accept((T) resource);
        }
        if (op.getRunner() != null) {
            op.getRunner().run();
        }
    }

    @Override
    public void close() {
        executeLifecycleOpsForTestResources(globalSharedTestResources.values(), TestLifecycleOperations::getAfterAlls);
    }
}
