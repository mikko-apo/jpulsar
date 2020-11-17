# Backlog

# v0.1

* Create test plan
  * Scan @TestResource and @Test method annotations
  * Scan class annotations
* Test running in serial without test resources
* Resolve TestResource requirements
  * https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection
  * @TestResource generics for Supplier<TestResource>
* Validate errors in configuration
  Test running in serial with test resources
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