package jpulsar.tester;

import jpulsar.ResourceHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MethodParameters {
    private final List<Object> parameters = new ArrayList<>();
    private List<ResourceHandler<?>> resourceHandlers = new ArrayList<>();
    private List<ResourceHandler<?>> privateResourceHandlersAfterAllCalledRightAfterTest = new ArrayList<>();

    public void addParameter(Object param, boolean shared) {
        if (param instanceof ResourceHandler) {
            ResourceHandler<?> resourceHandler = (ResourceHandler<?>) param;
            resourceHandlers.add(resourceHandler);
            param = resourceHandler.getResource();
            if(!shared) {
                privateResourceHandlersAfterAllCalledRightAfterTest.add(resourceHandler);
            }
        }
        parameters.add(param);
    }

    public Object[] parameterArray() {
        return parameters.toArray();
    }

    public Collection<ResourceHandler<?>> getResourceHandlers() {
        return resourceHandlers;
    }

    public List<ResourceHandler<?>> getPrivateResourceHandlersAfterAllCalledRightAfterTest() {
        return privateResourceHandlersAfterAllCalledRightAfterTest;
    }
}
