package jpulsar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TestFactory {
    /**
     * When using @TestFactory annotation, the class and method test resource parameters need to marked with shared=true or the parameter needs to be of type Supplier<Resource>.
     * The reason for this is that the factory method creates a list of DynamicTests which have closures that can reference either class variables or method variables.
     * JPulsar checks for this error. It's possible to bypass the error warning by setting allowSerialExecution=true, but this will have impact on performance.
     */
    boolean allowSerialExecution = false;

    String name() default "";

    /**
     * Attach generated test cases to report table. Each test case needs to define tableColumn and tableRow ids
     */
    String reportTable() default "";

    /**
     * Attach generated test cases to report table's specific column. DynamicTests need define tableRow.
     */
    String tableColumn() default "";

    /**
     * Attach generated test cases to report table's specific row. DynamicTests need define tableColumn.
     */
    String tableRow() default "";

    String[] usecases() default {};

    String[] tags() default {};
}
