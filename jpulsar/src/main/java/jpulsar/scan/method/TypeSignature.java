package jpulsar.scan.method;

import java.util.List;

public class TypeSignature {
    private final Class<?> typeClass;
    private final List<TypeSignature> classArguments;

    public TypeSignature(Class<?> typeClass, List<TypeSignature> classArguments) {
        this.typeClass = typeClass;
        this.classArguments = classArguments;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public List<TypeSignature> getClassArguments() {
        return classArguments;
    }
}
