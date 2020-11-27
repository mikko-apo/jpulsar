package jpulsar.tester;

import jpulsar.ResourceHandler;

import java.util.ArrayList;
import java.util.List;

public class MethodParameters {
    private final List<Object> parameters = new ArrayList<>();
    private List<ResourceHandler<?>> resourceHandlers = new ArrayList<>();

    public void addResourceHandler(ResourceHandler<?> resourceHandler) {
        resourceHandlers.add(resourceHandler);
    }

    public void addParameter(Object param) {
        parameters.add(param);
    }

    public Object[] parameterArray() {
        return parameters.toArray();
    }

    public List<ResourceHandler<?>> getResourceHandlers() {
        return resourceHandlers;
    }
}
