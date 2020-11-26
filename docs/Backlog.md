# Backlog

# v0.1

DONE:
* Create test plan
  * Scan @TestResource and @Test method annotations 
  * Scan class annotations
* Test running in serial without test resources

TODO:
* Resolve TestResource requirements
  * https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection
  * @TestResource generics for Supplier<TestResource>
  * Constructor @TestResource parameters
    * Can not reference class's non-static @TestResources
* Validate errors in configuration
* Test running in serial with test resources
* ResourceHandler lifecycle method support
* Test annotation lifecycle method support 
* Generate runtime log to stdout

# v0.2

* @TestFactory support
  * Report table output

# v0.3
* Ensure Kotlin support
  * @TestResource builder () -> TestResource
* Group test running by class
* Generate text test report
* Generate json test report

# v0.4
* Test running in parallel
  * Resource reserving

# v0.5

* Randomize test running
* Release to maven central
* Replace JUnit in project to jpulsar