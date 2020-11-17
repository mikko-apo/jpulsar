package jpulsar.util;

import org.junit.jupiter.api.DynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static jpulsar.util.Util.removeOne;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicTestBuilder<T> {
    List<NamedItem<T>> list = new ArrayList<>();

    public DynamicTestBuilder<T> add(String name, T t) {
        list.add(new NamedItem<>(name, t));
        return this;
    }

    public <I> Stream<DynamicTest> asDynamicTests(List<I> actualItems, BiPredicate<NamedItem<T>, I> matcher, BiConsumer<T, I> test) {
        assertEquals(list.size(), actualItems.size());
        ArrayList<NamedItem<T>> remainingSources = new ArrayList<>(list);
        return actualItems.stream().map(i -> {
            NamedItem<T> source = removeOne(remainingSources, s -> matcher.test(s, i));
            return DynamicTest.dynamicTest(source.name, () -> test.accept(source.data, i));
        });
    }
}
