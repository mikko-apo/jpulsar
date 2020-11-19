package jpulsar.scan.method;

import java.util.List;

public class MethodParameterInfo {
    private final Class<?>[] classArray;
    private final List<TypeSignature> typeSignatures;

    public MethodParameterInfo(Class<?>[] classArray, List<TypeSignature> typeSignatures) {

        this.classArray = classArray;
        this.typeSignatures = typeSignatures;
    }

    public Class<?>[] getClassArray() {
        return classArray;
    }

    public List<TypeSignature> getTypeSignatures() {
        return typeSignatures;
    }
}
