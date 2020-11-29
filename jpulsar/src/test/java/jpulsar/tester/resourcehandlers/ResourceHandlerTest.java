package jpulsar.tester.resourcehandlers;

import jpulsar.scan.Scanner;
import jpulsar.scan.TestScanResult;
import jpulsar.tester.SerialTester;
import jpulsar.tester.TestResultBuilder;
import jpulsar.tester.TestRunResult;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static jpulsar.scan.Scanner.scanPackages;
import static jpulsar.util.Util.getPackagePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceHandlerTest {
    @Test
    public void testResourceResouceHandler() {
        TestScanResult scanResult = scanPackages(getPackagePath(getClass()), Scanner::collectTestScanResult);
        TestRunResult result = SerialTester.runTests(scanResult);

        assertEquals(asList("beforeAll-tr1",
                "before-tr1",
                "test1-tr1",
                "after-tr1",
                "afterAll-tr1",
                "beforeAll-tr1",
                "before-tr1",
                "test2-tr1",
                "after-tr1",
                "afterAll-tr1"), TestResource1.log);
        assertEquals(asList("beforeAll-tr2",
                "before-tr2",
                "test1-tr2",
                "after-tr2",
                "before-tr2",
                "test2-tr2",
                "after-tr2",
                "afterAll-tr2"), TestResource2.log);
        assertEquals(asList("beforeAll-tr3",
                "before-tr3",
                "test1-tr3",
                "after-tr3",
                "before-tr3",
                "test2-tr3",
                "after-tr3",
                "afterAll-tr3"), TestResource3.log);
        assertEquals(1, TestResource2.count);

        TestResultBuilder testResultBuilder = new TestResultBuilder();
        testResultBuilder.addTestClassResult(ResourceHandlers.class, "test1", "test2");
        testResultBuilder.compareTestResults(result);
    }
}
