package jpulsar.scan.visibility;

import jpulsar.Test;
import jpulsar.TestResource;
import jpulsar.scan.resources.TestResource1;
import jpulsar.scan.resources.TestResource2;
import jpulsar.scan.resources.TestResource3;
import jpulsar.scan.resources.TestResource4;
import jpulsar.scan.resources.TestResource5;
import jpulsar.scan.resources.TestResource6;
import jpulsar.scan.resources.TestResource7;
import jpulsar.scan.resources.TestResource8;

public class Visibility {
    static public TestResource1 testResource1;
    static protected TestResource3 testResource3;
    static TestResource2 testResource2;
    static private TestResource4 testResource4;
    public TestResource5 testResource5;
    protected TestResource7 testResource7;
    TestResource6 testResource6;
    private TestResource8 testResource8;

    public Visibility(TestResource1 tr1) {
    }

    Visibility(TestResource2 tr2) {

    }

    protected Visibility(TestResource3 tr3) {

    }

    private Visibility(TestResource4 tr4) {
    }

    @TestResource
    public static TestResource1 tr1() {
        return new TestResource1();
    }

    @TestResource
    static TestResource2 tr2() {
        return new TestResource2();
    }

    @TestResource
    protected static TestResource3 tr3() {
        return new TestResource3();
    }

    @TestResource
    private static TestResource4 tr4() {
        return new TestResource4();
    }

    @Test
    static public void test1() {

    }

    @Test
    static void test2() {

    }

    @Test
    static protected void test3() {

    }

    @Test
    static private void test4() {

    }

    @TestResource
    public TestResource5 tr5() {
        return new TestResource5();
    }

    @TestResource
    TestResource6 tr6() {
        return new TestResource6();
    }

    @TestResource
    protected TestResource7 tr7() {
        return new TestResource7();
    }

    @TestResource
    private TestResource8 tr8() {
        return new TestResource8();
    }

    @Test
    public void test5() {

    }

    @Test
    void test6() {

    }

    @Test
    protected void test7() {

    }

    @Test
    private void test8() {

    }

    @Test
    static public void useAllTestResources(TestResource1 tr1,
                             TestResource2 tr2,
                             TestResource3 tr3,
                             TestResource4 tr4,
                             TestResource5 tr5,
                             TestResource6 tr6,
                             TestResource7 tr7,
                             TestResource8 tr8) {

    }
}
