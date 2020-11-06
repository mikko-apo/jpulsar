# Backlog

# v0.1

* Create test plan
  * Scan @TestResource and @Test method annotations
  * Scan class annotations
  * Resolve TestResource requirements
    * https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection
    * @TestResource generics for Supplier<TestResource>
  * Validate errors in configuration
  * Create plan
* Test running in serial
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
* Test running in parallel
  * Resource reserving
* Randomize test running

# v0.4

* Release to maven central
* Replace JUnit in project to jpulsar