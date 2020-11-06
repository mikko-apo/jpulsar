package example.tests;

import example.TestParams;
import example.UseCases;
import example.main.DbClient;
import example.main.HttpClient;
import jpulsar.Test;
import jpulsar.Usecase;

import static jpulsar.TestStep.testStep;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Usecase(usecases = {UseCases.TicketSale, UseCases.ErrorScenarios})
public class TicketSaleErrorsTest {
    HttpClient httpClient;
    DbClient testDbClient;

    @Test
    public void ticketAlreadySold() {
        httpClient.sell(TestParams.testSellId);
        testStep("sell the same ticket again", () -> {
            httpClient.sell(TestParams.testSellId);
            assertEquals(1, testDbClient.txCount());
        });
    }
}
