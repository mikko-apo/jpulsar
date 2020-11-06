package example.tests;

import example.MobileTester;
import example.TestParams;
import example.UseCases;
import example.main.DbClient;
import example.main.MobileClient;
import example.main.Server;
import jpulsar.Test;
import jpulsar.TestResource;
import jpulsar.Usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Usecase(UseCases.TicketSale)
class TicketSaleMobileTest {
    DbClient testDbClient;

    // MobileClient is heavy to initialize, so restrict it to one instance.
    // static test resource method does not have access to class test resources,
    // so jpulsar is able initialize MobileClient without an instance of TicketSaleMobileTest()
    @TestResource(max = 1)
    static MobileClient mobileClient(Server server) {
        return new MobileClient(server);
    }

    // @TestResource methods in a class with @Test methods are available only to that class
    @TestResource
    MobileTester mobileTester(MobileClient mobileClient) {
        return new MobileTester(mobileClient);
    }

    @Test
    @Usecase(UseCases.Flaky)
    void sellSucceeds(MobileTester tester) {
        tester.sell(TestParams.testSellId);
        assertEquals(1, testDbClient.txCount());
    }
}
