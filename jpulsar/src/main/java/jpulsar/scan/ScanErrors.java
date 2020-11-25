package jpulsar.scan;

import jpulsar.scan.method.ModifierHelper;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.util.NamedItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;

import static java.util.Arrays.asList;
import static jpulsar.util.Collections.map;
import static jpulsar.util.Strings.join;
import static jpulsar.util.Strings.mapJoin;

public class ScanErrors {
    public static String invalidAttributes(List<ModifierHelper> foundInvalidModifiers) {
        return "invalid attributes: " + mapJoin(foundInvalidModifiers,
                modifierHelper -> modifierHelper.name,
                ",");
    }

    public static List<Object> noMatchingTestResources(int i,
                                                  String nameFromTestMethodParameterAnnotation,
                                                  Class<?> testResourceParam) {
        return asList("could not find matching test resource for parameter",
                i,
                nameFromTestMethodParameterAnnotation,
                testResourceParam);
    }

    public static List<Object> tooManyMatchingResources(int i,
                                                        String nameFromTestMethodParameterAnnotation,
                                                        Class<?> testResourceParam,
                                                        List<TestResourceMethod> testResources) {
        return asList("Found",
                testResources.size(),
                "test resources for parameter",
                i,
                nameFromTestMethodParameterAnnotation,
                testResourceParam,
                ". Matching test resources:",
                map(testResources, testResourceMethod -> testResourceMethod.getMethod().getName())
        );
    }

    public static List<Object> parametrizedArgument(int i, List<String> params) {
        return asList("parameter",
                Integer.toString(i),
                " is parameterized: " + join(params, ","));
    }

    public static List<Object> invalidParameter(int i, Type parameterType) {
        return asList("unsupported parameter", Integer.toString(i), parameterType.getTypeName());
    }

    public static List<Object> tooManyFeatures(List<NamedItem<Boolean>> enabledFeatures) {
        return asList("Can enable only one feature. Now has", mapJoin(enabledFeatures, booleanNamedItem -> booleanNamedItem.name, ", "));
    }

    public static List<Object> tooManyConstructors(Constructor<?>[] constructors) {
        return asList("has", constructors.length, "constructors. Should have 1 constructor");
    }
}
