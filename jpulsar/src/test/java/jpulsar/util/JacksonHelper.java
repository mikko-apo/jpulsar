package jpulsar.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JacksonHelper {
    private final ObjectMapper mapper;

    private JacksonHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <A, B> void jsonEquals(A a, B b) {
        assertEquals(jsonPretty(a), jsonPretty(b));
    }

    public String jsonPretty(Object a) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(a);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static public JacksonHelper initializeJackson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        return new JacksonHelper(mapper);
    }

    static public JacksonHelper initializeScannerJackson() {
        JacksonHelper jacksonHelper = initializeJackson();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Method.class, createObjectStringSerializer(Method::toGenericString));
        module.addSerializer(Constructor.class, createObjectStringSerializer(Constructor::toGenericString));
        jacksonHelper.mapper.registerModule(module);
        return jacksonHelper;
    }

    private static <T> JsonSerializer<T> createObjectStringSerializer(final Function<T, String> asString) {
        return new JsonSerializer<T>() {
            @Override
            public void serialize(T t, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
                jgen.writeString(asString.apply(t));
            }
        };
    }
}