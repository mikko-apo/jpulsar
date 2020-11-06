package example.tests;

import example.TestParams;
import example.UseCases;
import example.main.DbClient;
import example.main.HttpClient;
import jpulsar.Test;
import jpulsar.Usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Usecase(UseCases.TicketSale)
class TicketSaleTest {
    // Test class instance fields are initialized with Dependency Injection every time a test is run based on configured @TestResources
    // @TestResource configuration manages the database initialization and clean up, test class does not need to worry about those details
    HttpClient httpClient;
    DbClient testDbClient;

    @Test
    public void sellSucceeds() {
        httpClient.sell(TestParams.testSellId);
        assertEquals(1, testDbClient.txCount());
    }
}
