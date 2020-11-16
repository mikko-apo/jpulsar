package jpulsar.scan.method;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public enum ModifierHelper {
    PUBLIC("public"),
    PROTECTED("protected"),
    PACKAGE("package"),
    PRIVATE("private"),
    ABSTRACT("abstract");

    public final String name;

    ModifierHelper(String name) {
        this.name = name;
    }

    public static List<ModifierHelper> hasModifiers(int modifiers, ModifierHelper... visibility) {
        return Arrays.stream(visibility).filter(modifierHelper -> hasModifier(modifiers, modifierHelper)).collect(toList());
    }

    private static boolean hasModifier(int modifiers, ModifierHelper modifierHelper) {
        switch (modifierHelper) {
            case PUBLIC:
                return Modifier.isPublic(modifiers);
            case PROTECTED:
                return Modifier.isProtected(modifiers);
            case PACKAGE:
                return !(Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers) || Modifier.isPrivate(modifiers));
            case PRIVATE:
                return Modifier.isPrivate(modifiers);
            case ABSTRACT:
                return Modifier.isAbstract(modifiers);
        }
        return false;
    }
}
