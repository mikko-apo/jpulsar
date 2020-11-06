package example.tests;

import example.UseCases;
import example.main.DbClient;
import example.main.HttpClient;
import jpulsar.DynamicTest;
import jpulsar.TestFactory;
import jpulsar.Usecase;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Usecase(UseCases.ErrorScenarios)
class ApiErrorsTest {
    // httpClient and testDbClient need to be Supplier<Resource>, because class uses @TestFactory
    Supplier<HttpClient> httpClient;
    Supplier<DbClient> testDbClient;

    /**
     * @TestFactory generates a list of DynamicTest instances.
     * Here, a test report table named "Api errors" is generated that collects test information for different API endpoints.
     */
    @TestFactory(reportTable = UseCases.ApiErrors, tableRow = UseCases.TicketSale)
    Stream<DynamicTest> createApiErrorTests() {
        return Stream.of(
                new ApiError<>(500,
                        (payload) -> httpClient.get().sell(payload),
                        asList("", "a")
                )
        ).flatMap(apiError -> apiError.payloads.map(payload ->
                DynamicTest.column(apiError.expectedCode, () -> {
                    apiError.request.accept(payload);
                    assertEquals(0, testDbClient.get().txCount());
                }))
        );
    }

    static class ApiError<RequestPayload> {
        int expectedCode;
        Consumer<RequestPayload> request;
        Stream<RequestPayload> payloads;

        public ApiError(int expectedCode, Consumer<RequestPayload> request, List<RequestPayload> payloads) {
            this.expectedCode = expectedCode;
            this.request = request;
            this.payloads = payloads.stream();
        }
    }
}
