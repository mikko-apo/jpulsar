package jpulsar.scan.method;

import java.lang.reflect.Modifier;
import java.util.List;

import static jpulsar.util.Collections.filter;

public enum ModifierEnum {
    PUBLIC("public"),
    PROTECTED("protected"),
    PACKAGE("package"),
    PRIVATE("private"),
    ABSTRACT("abstract");

    public final String name;

    ModifierEnum(String name) {
        this.name = name;
    }

    public static List<ModifierEnum> hasModifiers(int modifiers, ModifierEnum... visibility) {
        return filter(visibility, modifierEnum -> hasModifier(modifiers, modifierEnum));
    }

    private static boolean hasModifier(int modifiers, ModifierEnum modifierEnum) {
        switch (modifierEnum) {
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
