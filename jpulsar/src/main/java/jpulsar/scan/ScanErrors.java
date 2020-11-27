package jpulsar.scan;

import jpulsar.scan.method.ModifierEnum;
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
    public static String invalidAttributes(List<ModifierEnum> foundInvalidModifiers) {
        return "invalid attributes: " + mapJoin(foundInvalidModifiers,
                modifierEnum -> modifierEnum.name,
                ",");
    }

    public static List<Object> noMatchingTestResources(int i,
                                                       String nameFromTestMethodParameterAnnotation,
                                                       Type testResourceParam) {
        return asList("could not find @TestResource for parameter",
                i,
                nameFromTestMethodParameterAnnotation,
                testResourceParam);
    }

    public static List<Object> tooManyMatchingResources(int i,
                                                        String nameFromTestMethodParameterAnnotation,
                                                        Type testResourceParam,
                                                        List<TestResourceMethod> testResources) {
        return asList("Found",
                testResources.size(),
                "@TestResources for parameter",
                i,
                nameFromTestMethodParameterAnnotation,
                testResourceParam,
                ". Matching @TestResources:",
                map(testResources, testResourceMethod -> testResourceMethod.getMethod().getName())
        );
    }

    public static List<Object> parametrizedArgument(int i, List<String> params) {
        return asList("parameterized method parameter",
                i,
                "is not supported:",
                join(params, ","));
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

    public static List<Object> testAndTestResourceAnnotation() {
        return asList("has both @Test and @TestResource annotations. Can have only one.");
    }

    public static List<Object> parametrizedReturnType(Type returnType) {
        return asList("parametrized returnType is not supported", returnType.getTypeName());
    }

    public static List<Object> unusedTestResource() {
        return asList("unused @TestResource. There are no @Tests or @TestResources using this @TestResource.");
    }

    public static List<Object> testResourceDependenciesNotSupported(Type[] genericParameterTypes) {
        return asList("this @TestResource references other @TestResources. This is not supported.");
    }

    public static List<Object> sameTestResourceMoreThanOnce(int i, Type aClass) {
        return asList("@Test references same @TestResource more than once. Parameter at", i, aClass.getTypeName());
    }
}
