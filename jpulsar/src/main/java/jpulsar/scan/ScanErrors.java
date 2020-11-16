package jpulsar.scan;

import jpulsar.scan.method.ModifierHelper;

import java.util.List;

import static jpulsar.util.Strings.mapJoin;

public class ScanErrors {
    public static String invalidAttributes(List<ModifierHelper> foundInvalidModifiers) {
        return "invalid attributes: " + mapJoin(foundInvalidModifiers,
                modifierHelper -> modifierHelper.name,
                ",");
    }
}
