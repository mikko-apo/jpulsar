package example.main;

public class DbClient {
    Database db;

    public DbClient(Database db) {
        this.db = db;
    }

    public void migrateDb() {
    }

    public void clearDb() {
        db.tx.clear();
    }

    public int txCount() {
        return db.tx.size();
    }
}
