package example

data class Database(
        val name: String,
        val tx: MutableList<String> = mutableListOf()
) {
    fun close() {
    }
}

data class DbClient(val db: Database) {
    fun migrateDb() {}

    fun clearDb() {
        db.tx.clear()
    }

    fun txCount() = db.tx.size
}

data class Server(val id: Int, val db: Database) {
    fun addTx(s: String) {
        if (db.tx.contains(s)) {
            throw RuntimeException("$s already exists")
        }
        db.tx.add(s)
    }
    fun close() {}
}

data class HttpClient(val server: Server) {
    fun sell(s: String) {
        server.addTx(s)
    }
}

data class MobileClient(val server: Server)