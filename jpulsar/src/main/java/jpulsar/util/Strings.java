package jpulsar.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Strings {
    public static <T> String join(T[] arr, String delimiter) {
        return join(Arrays.asList(arr), delimiter);
    }

    public static <T> String join(Collection<T> collection, String delimiter) {
        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    public static <T> String mapJoin(Collection<T> collection, Function<T, Object> mapper, String delimiter) {
        return collection.stream()
                .map(mapper)
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }
}
