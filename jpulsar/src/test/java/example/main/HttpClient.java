package example.main;

public class HttpClient {
    private Server server;

    public HttpClient(Server server) {
        this.server = server;
    }

    public void sell(String s) {
        server.addTx(s);
    }
}
