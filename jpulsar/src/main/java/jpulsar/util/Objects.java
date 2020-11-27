package jpulsar.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Objects {
    public static <T> T with(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    public static <T,R> R apply(T t, Function<T, R> function) {
        return function.apply(t);
    }
}
