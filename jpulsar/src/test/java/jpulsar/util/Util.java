package jpulsar.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import static jpulsar.util.Strings.join;

public class Util {
    public static JacksonHelper scannerJackson = JacksonHelper.initializeScannerJackson();

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

    public static <T> String getPackagePath(Class<T> clazz) {
        String[] full = clazz.getName().split("\\.");
        String[] packagePath = Arrays.copyOf(full, full.length - 1);

        return String.join(".", packagePath);
    }
}
