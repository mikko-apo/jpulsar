package jpulsar.scan.visibility;

import jpulsar.scan.ScanErrors;
import jpulsar.scan.Scanner;
import jpulsar.scan.TestClassBuilder;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.ModifierEnum;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static jpulsar.scan.ScanErrors.invalidAttributes;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.ScannerTestUtil.emptyClassTestResourceAnnotation;
import static jpulsar.util.ScannerTestUtil.emptyTestAnnotation;
import static jpulsar.util.Util.getPackagePath;
import static jpulsar.util.Util.scannerJackson;

public class VisibilityTests {
    @Test
    void testVisibility() {
        TestScanResult result = scanPackages(getPackagePath(getClass()), Scanner::collectTestClasses);
        TestClassBuilder builder = new TestClassBuilder();

        List<ModifierEnum> modifiers = asList(
                null,
                null,
                ModifierEnum.PROTECTED,
                ModifierEnum.PRIVATE,
                null,
                null,
                ModifierEnum.PROTECTED,
                ModifierEnum.PRIVATE
        );

        builder.addTestClass(Visibility.class)
                .setConstructor(null)
                .addIssue(ScanErrors.tooManyConstructors(new Constructor[4]));
        IntStream.range(1, 9).forEach(i -> {
            TestMethod t = builder.addTestMethod(emptyTestAnnotation, "test" + i);
            TestResourceMethod tr = builder.addTestResourceMethod(emptyClassTestResourceAnnotation, "tr" + i, true);
            ModifierEnum modifierEnum = modifiers.get(i - 1);
            if(modifierEnum != null) {
                String invalidAttribute = invalidAttributes(asList(modifierEnum));
                t.addIssue(invalidAttribute);
                tr.addIssue(invalidAttribute);
            }
        });
        scannerJackson.jsonEquals(builder.testClasses, result.getTestClasses());
    }
}
