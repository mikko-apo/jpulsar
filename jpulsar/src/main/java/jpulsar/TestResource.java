package jpulsar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TestResource defines a resource that is used by the tests.
 * By default resources are discarded after each test, but defining shared or max values means there will be max number of instances.
 * <p>
 * TestResource annotation can be attached to a resource builder function.
 * If it is attached to a @Test function parameter, it is used to specify that a named resource is used
 * <p>
 * TestResources used by a test are logged to test results
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface TestResource {
    /**
     * Defining max means that resource is heavy to initialize.
     * - There will max number of resources initialized
     * - Resource is used in only one test at a time. Resources are reused for different test runs, but not at the same time.
     * Default is 0, which means that a new instance is initialized for every test run
     */
    int max() default 0;

    /**
     * Shared means that the same test resource instance can be given to any number of tests at the same time.
     * - max=0 and shared=true means that resource is initialized only once
     * - max=3 and shared=true means that three resources are used randomly
     * Default is that instances are not shared
     */
    boolean shared() default false;

    /**
     * Fixed means that initialized test resource instance is not destroyed after test, but instead reused.
     * Test resource instance is given to one test at a time.
     * - Max count = number of test runner threads or if test resource depends on other resources with limited number of instances.
     * Default is that instances are not shared
     */
    boolean fixed() default false;

    /**
     * Hidden resource is not saved in to test results
     */
    boolean hidden() default false;

    String name() default "";

    /**
     * Scope defines how TestResource is used in test classes.
     * Default: If TestResource function is in a class with @Test functions, scope=Scope.CLASS. Otherwise scope=Scope.GLOBAL
     */
    TestResourceScope scope() default TestResourceScope.GLOBAL;

    String[] usecases() default {};
}
