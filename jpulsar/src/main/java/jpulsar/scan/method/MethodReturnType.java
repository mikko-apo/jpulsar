package jpulsar.scan.method;

import java.util.List;

public class MethodReturnType {
    private final Class<?> typeClass;
    private final List<MethodReturnType> classArguments;

    public MethodReturnType(Class<?> typeClass, List<MethodReturnType> classArguments) {
        this.typeClass = typeClass;
        this.classArguments = classArguments;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public List<MethodReturnType> getClassArguments() {
        return classArguments;
    }
}
