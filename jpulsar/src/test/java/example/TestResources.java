package example;

import example.main.Database;
import example.main.DbClient;
import example.main.HttpClient;
import example.main.Server;
import jpulsar.ResourceHandler;
import jpulsar.ResourceInfo;
import jpulsar.TestResource;

public class TestResources {

    /**
     * There are three test databases available. This mean that there can be only three tests using a database running at the same time.
     *
     * @TestResource(max = 3) means that there is going to be three Database() instances that point to "testDb1", "testDb2" and "testDb3".
     * Database instances are reused between tests and there is going to be only three of them.
     */
    @TestResource(max = 3)
    public ResourceHandler<Database> testDb(ResourceInfo info) {
        return new ResourceHandler<>(new Database("testDb" + info.getIndex())).
                afterAll(Database::close); // close db connections after db is no longer needed
    }

    /**
     * TestResources can refer to other test resources. The test resource lifecycle methods are called in according to definition.
     * <p>
     * Every test that uses a DbClient get a DbClient instance, but the number of parallel DbClient instances is limited by testDb max=3.
     * DbClient instances are instantiated for every test.
     */
    @TestResource
    public ResourceHandler<DbClient> testDbClient(Database testDB) {
        return new ResourceHandler<>(new DbClient(testDB)).
                beforeAll(DbClient::migrateDb). // migrate before an tests are run on this database
                before(DbClient::clearDb); // clear database automatically before any test
    }

    /**
     * There can be only three servers and tests using the server running at the same time. This is limited by the number of databases.
     * Database is automatically cleared before any server test, because dbClient clears it.
     * @return
     */
    @TestResource(fixed = true)
    public ResourceHandler<Server> testServer(ResourceInfo info, Database db, DbClient dbClient) {
        return new ResourceHandler<>(new Server(info.getIndex(), db)).afterAll(Server::close);
    }

    /**
     * There can be only three HttpClients and tests using the HttpClient running at the same time. This is limited by the number of databases & servers.
     * Database is automatically cleared before any server test, because dbClient clears it.
     */
    @TestResource
    public HttpClient httpClient(Server server) {
        return new HttpClient(server);
    }
}

