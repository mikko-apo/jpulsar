package jpulsar.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class Maps {
    public static <K, V> V getOrPut(Map<K, V> map, K key, Supplier<V> supplier) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return map.put(key, supplier.get());
        }
    }

    public static <K, V> List<V> addMulti(Map<K, List<V>> map, K key, V... values) {
        return addMulti(map, key, asList(values));
    }

    public static <K, V> List<V> addMulti(Map<K, List<V>> map, K key, Collection<V> values) {
        List<V> list = getOrPut(map, key, ArrayList::new);
        list.addAll(values);
        return list;
    }

    public static <K, V> Map<K, List<V>> groupBy(Collection<V> values, Function<V, K> keyF) {
        Map<K, List<V>> map = new HashMap<>();
        for (V v : values) {
            addMulti(map, keyF.apply(v), v);
        }
        return map;
    }
}
