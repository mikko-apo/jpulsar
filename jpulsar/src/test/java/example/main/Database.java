package example.main;

import java.util.ArrayList;
import java.util.List;

public class Database {
    String name;
    List<String> tx = new ArrayList<>();

    public Database(String name) {
        this.name = name;
    }

    public void close() {
    }
}
