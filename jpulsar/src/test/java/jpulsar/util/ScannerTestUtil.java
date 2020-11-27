package jpulsar.util;

import jpulsar.TestResourceScope;
import jpulsar.scan.TestClassBuilder;

public class ScannerTestUtil {
    public static final jpulsar.Test emptyTestAnnotation = TestClassBuilder.createTestAnnotation("", new String[0], new String[0]);
    public static final jpulsar.TestResource emptyTestResourceAnnotation = TestClassBuilder.createTestResourceAnnotation("",
            0,
            false,
            false,
            false,
            TestResourceScope.GLOBAL,
            new String[0]
    );
    public static final jpulsar.TestResource emptyClassTestResourceAnnotation = TestClassBuilder.createTestResourceAnnotation("",
            0,
            false,
            false,
            false,
            TestResourceScope.CLASS,
            new String[0]
    );
}
