package jpulsar.scan.visibility;

import jpulsar.scan.ScanErrors;
import jpulsar.scan.Scanner;
import jpulsar.scan.TestClassBuilder;
import jpulsar.scan.TestScanResult;
import jpulsar.scan.method.ModifierEnum;
import jpulsar.scan.method.TestMethod;
import jpulsar.scan.method.TestResourceMethod;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;
import jpulsar.scan.resources.TestResource3;
import jpulsar.scan.resources.TestResource4;
import jpulsar.scan.resources.TestResource5;
import jpulsar.scan.resources.TestResource6;
import jpulsar.scan.resources.TestResource7;
import jpulsar.scan.resources.TestResource8;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static jpulsar.scan.ScanErrors.invalidAttributes;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.ScannerTestUtil.emptyClassTestResourceAnnotation;
import static jpulsar.util.ScannerTestUtil.emptyTestAnnotation;
import static jpulsar.util.Util.getPackagePath;

public class VisibilityTests {
    @Test
    void testVisibility() {
        TestScanResult result = scanPackages(getPackagePath(getClass()), Scanner::collectTestScanResult);
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
        List<TestResourceMethod> allTrs = new ArrayList<>();
        IntStream.range(1, 9).forEach(i -> {
            TestMethod t = builder.addTestMethod(emptyTestAnnotation, "test" + i);
            TestResourceMethod tr = builder.addTestResourceMethod(emptyClassTestResourceAnnotation, "tr" + i, true);
            allTrs.add(tr);
            ModifierEnum modifierEnum = modifiers.get(i - 1);
            if (modifierEnum != null) {
                String invalidAttribute = invalidAttributes(asList(modifierEnum));
                t.addIssue(invalidAttribute);
                tr.addIssue(invalidAttribute);
            }
        });
        TestMethod t = builder.addTestMethod(emptyTestAnnotation,
                "useAllTestResources",
                TestResource1.class,
                TestResource2.class,
                TestResource3.class,
                TestResource4.class,
                TestResource5.class,
                TestResource6.class,
                TestResource7.class,
                TestResource8.class
        );
        for(TestResourceMethod tr: allTrs) {
            t.addParameterTestResource(tr);
        }
        builder.compareTestClasses(result.getTestClasses());
    }
}
