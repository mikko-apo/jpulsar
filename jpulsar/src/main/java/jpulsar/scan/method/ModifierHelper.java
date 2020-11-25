package jpulsar.scan.method;

import java.lang.reflect.Modifier;
import java.util.List;

import static jpulsar.util.Collections.filter;

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
        return filter(visibility, modifierHelper -> hasModifier(modifiers, modifierHelper));
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
