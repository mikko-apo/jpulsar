package example.main;

public class Server {
    private final int id;
    private final Database db;

    public Server(int id, Database db) {
        this.id = id;
        this.db = db;
    }

    public void addTx(String s) {
        if (db.tx.contains(s)) {
            throw new RuntimeException("$s already exists");
        }
        db.tx.add(s);
    }

    public void close() {

    }
}

