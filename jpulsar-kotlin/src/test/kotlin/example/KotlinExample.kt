package example

import jpulsar.DynamicTest
import jpulsar.ResourceHandler
import jpulsar.ResourceInfo
import jpulsar.Test
import jpulsar.TestFactory
import jpulsar.TestResource
import jpulsar.TestStep.testStep
import jpulsar.Usecase
import kotlin.test.assertEquals

/**
 * There are three test databases available. This mean that there can be only three tests using a database running at the same time.
 *
 * @TestResource(max = 3) means that there is going to be three Database() instances that point to "testDb1", "testDb2" and "testDb3".
 * Database instances are reused between tests and there is going to be only three of them.
 */
@TestResource(max = 3)
fun testDb(info: ResourceInfo) = ResourceHandler(Database(name = "testDb-${info.index}"))
        .afterAll { db -> db.close() } // close db connections after db is no longer needed

/**
 * TestResources can refer to other test resources. The test resource lifecycle methods are called in according to definition.
 * <p>
 * Every test that uses a DbClient get a DbClient instance, but the number of parallel DbClient instances is limited by testDb max=3.
 * DbClient instances are instantiated for every test.
 */
@TestResource
fun testDbClient(testDB: Database) = ResourceHandler(DbClient(testDB))
        .beforeAll { dbClient -> dbClient.migrateDb() }
        .before { dbClient -> dbClient.clearDb() }

/**
 * There can be only three servers and tests using the server running at the same time.
 * This is limited by the number of databases.
 * Server instances are reused between tests and there is going to be only three of them.
 * Database is automatically cleared before any server test, because dbClient clears it.
 */
@TestResource(fixed = true)
fun testServer(info: ResourceInfo, db: Database, dbClient: DbClient) = ResourceHandler(Server(info.index, db))
        .afterAll { server -> server.close() }

/**
 * There can be only three HttpClients and tests using the HttpClient running at the same time. This is limited by the number of databases & servers.
 * Database is automatically cleared before any server test, because dbClient clears it.
 */
@TestResource
fun httpClient(server: Server) = HttpClient(server)

object UseCases {
    const val TicketSale = "Ticket sale"
    const val ErrorScenarios = "Error scenarios"
    const val Flaky = "Flaky"
    const val ApiErrors = "Api errors"
}

const val testSellId = "123"

// Test class instance fields are initialized with Dependency Injection every time a test is run based on configured @TestResources
// @TestResource configuration manages the database initialization and clean up, test class does not need to worry about those details
@Usecase(UseCases.TicketSale)
class TicketSale(val httpClient: HttpClient, val testDbClient: DbClient) {

    @Test
    fun sellSucceeds() {
        httpClient.sell(testSellId)
        assertEquals(1, testDbClient.txCount())
    }
}

@Usecase(usecases = [UseCases.TicketSale, UseCases.ErrorScenarios])
class TicketSaleErrors(val httpClient: HttpClient, val testDbClient: DbClient) {

    @Test
    fun ticketAlreadySold() {
        httpClient.sell(testSellId)
        testStep("sell the same ticket again") {
            httpClient.sell(testSellId)
            assertEquals(1, testDbClient.txCount())
        }
    }
}

@Usecase(UseCases.TicketSale)
class TicketSaleMobile(val mobileClient: MobileClient, val testDbClient: DbClient) {

    // @TestResource methods in a class without @Test methods are available with TestResourceScope.GLOBAL
    @TestResource
    fun mobileTester(mobileClient: MobileClient) = MobileTester(mobileClient)

    @Test
    @Usecase(UseCases.Flaky)
    fun sellSucceeds(tester: MobileTester) {
        tester.sell(testSellId)
        assertEquals(1, testDbClient.txCount())
    }

    companion object {
        // MobileClient is heavy to initialize, so restrict it to one instance.
        // static test resource method does not have access to class test resources,
        // so jpulsar is able initialize MobileClient without an instance of TicketSaleMobileTest()
        @TestResource(max = 1)
        fun mobileClient(server: Server) = MobileClient(server)
    }
}

// httpClient and testDbClient need to be () -> Resource, because class uses @TestFactory
@Usecase(UseCases.ErrorScenarios)
class ApiErrors(val httpClient: () -> HttpClient, val testDbClient: () -> DbClient) {

    class ApiError<Payload>(
            val expectedCode: Int,
            val request: (Payload) -> Unit,
            vararg val payloads: Payload
    )

    /**
     * @TestFactory generates a list of DynamicTest instances.
     * Here, a test report table named "Api errors" is generated that collects test information for different API endpoints.
     */
    @TestFactory(reportTable = UseCases.ApiErrors, tableColumn = UseCases.TicketSale)
    fun createApiErrorTests() = listOf(ApiError(
            500,
            { payload -> httpClient().sell(payload) },
            "", "a"
    )).map { apiError ->
        apiError.payloads.map {
            DynamicTest.row(apiError.expectedCode) {
                apiError.request(it)
                assertEquals(0, testDbClient().txCount())
            }
        }
    }
}
