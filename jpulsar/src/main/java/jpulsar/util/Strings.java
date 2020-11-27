package jpulsar.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Strings {
    public static String joinSpaced(List<Object> arr) {
        return join(arr, " ");
    }

    public static <T> String join(Collection<T> collection, String delimiter) {
        return mapJoin(collection, t -> t, delimiter);
    }

    public static <T> String mapJoin(Collection<T> collection, Function<T, Object> mapper, String delimiter) {
        return collection.stream()
                .map(mapper)
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }
}
