package jpulsar.scan.method;

import java.util.List;

public class ConstructorInfo extends TestMethodBase {
    public ConstructorInfo(int modifiers, List<Class<?>> methodParameterTypes) {
        super(null, modifiers, methodParameterTypes);
    }
}
