package jpulsar.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Streams {
    public static <T> List<T> toList(Stream<T> items) {
        return items.collect(Collectors.toList());
    }

    public static <T, R> List<R> map(List<T> items, Function<T, R> mapper) {
        return toList(items.stream().map(mapper));
    }

    public static <T> List<T> filter(List<T> items, Predicate<T> filter) {
        return toList(items.stream().filter(filter));
    }

    public static <T, R> List<R> map(T[] items, Function<T, R> mapper) {
        return toList(Arrays.stream(items).map(mapper));
    }

    public static <T> List<T> filter(T[] items, Predicate<T> filter) {
        return toList(Arrays.stream(items).filter(filter));
    }
}
