JPulsar is an integration and performance oriented testing framework for Java.

See test examples
* Java [jpulsar/src/test/java/example](jpulsar/src/test/java/example)
  * [TicketSaleTest.java](jpulsar/src/test/java/example/tests/TicketSaleTest.java) - Integration test that shows how test resource management cleans up test code
  * [TestResources.java](jpulsar/src/test/java/example/TestResources.java) - Shared test resource configuration
  * [ApiErrorsTest.java](jpulsar/src/test/java/example/tests/ApiErrorsTest.java) - @TestFactory example with test report table generation
* [KotlinExample.kt](jpulsar-kotlin/src/test/kotlin/example/KotlinExample.kt)

# JPulsar concepts

1. Performance from parallelization
2. Generate test report that describes how tests map to use cases
3. Prioritize feedback speed and provide better test report tools

## 1. Performance from parallelization

Unit tests are easy to parallelize. Ideally code that is being unit tested should be a small piece of code, you 
give a few parameters and expect a specific result. Since there are no dependencies, all tests can be run at
the same time.

Integration tests that use external resources or tests that depend on something where the initialization cost is high
benefit from resource management. If there is a need to parallelize integration tests, resources need to be managed 
in a parallel way.

## 2. Generate test report that describes how tests map to use cases 

Big software projects have large test sets and it becomes difficult to see what is being tested and how.

### Usecases

Describing how a tests relates to a usecases makes it possible to document the test set more thoroughly.

### Tables

Tables can give a quick overview of how implementation progresses

Api error code tests:

| Error code  | Sell  | Cancel  | Cashout  |
|:---:|---|---|---|
| 400 | OK 4 | OK 1 | OK 2 |
| 401 | FAIL 5  | - | FAIL 3  |
| 403 | OK 1  | OK 1 | - |

## 3. Prioritize feedback speed and provide better test result tools

### Test run order

Tests that failed on the previous test run are run first

### Test execution times and results can be stored

Performance and stability regression are difficult to spot. JPulsar stores test execution results and provides tools
to inspect history.

### Test steps give better visibility how complex tests progress

```java
    @Test
    public void test() {
      doSomething()
      step("Doing it again")
      doSomething()
    }
```

step() allows JPulsar to log finegrained information test progress. Each step duration is logged.

# JPulsar glossary

* @TestResource
  * Automatically managed resources that are used and shared by tests
  * Test resource can use other test resources
  * Test resources are given to tests using Dependency Injection
* @Test - Each test has a name and can define test resources it uses
* @TestFactory produces a list of tests dynamically 
* Step - Longer test can be split in to steps. Steps log execution time and possible thrown exceptions
* @Usecase - Usecase can be given as a parameter to the describe(). This allows JPulsar to create reports that show how that usecase is being tested 
* Test report table can be used to collect test results to a table report