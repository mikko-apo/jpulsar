package jpulsar.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Util {
    public static <T> T removeOne(ArrayList<T> list, Predicate<T> predicate) {
        ArrayList<Integer> foundIndexes = new ArrayList<>();
        ArrayList<T> foundItems = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            if (predicate.test(t)) {
                foundIndexes.add(i);
                foundItems.add(t);
            }
        }
        if (foundIndexes.size() != 1) {
            throw new RuntimeException("Found " + foundIndexes.size() + " matches instead of 1. At locations: " + join(foundIndexes, ", "));
        }
        int index = foundIndexes.get(0);
        list.remove(index);
        return foundItems.get(0);
    }

    public static <T> String join(Collection<T> collection, String delimiter) {
        return collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    public static <A, B> void  jsonEquals(A a, B b) {
        assertEquals(jsonPretty(a), jsonPretty(b));
    }

    public static String jsonPretty(Object a) {
        try {
            return sortedJackson.writerWithDefaultPrettyPrinter().writeValueAsString(a);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static private ObjectMapper initializeJackson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        return mapper;
    }

    static public ObjectMapper sortedJackson = initializeJackson();
}
