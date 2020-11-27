package jpulsar.scan.method;

import jpulsar.scan.Issues;

import java.lang.reflect.Constructor;

public class ConstructorInfo extends Issues {
    private final Constructor<?> constructor;

    public ConstructorInfo(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }
}
