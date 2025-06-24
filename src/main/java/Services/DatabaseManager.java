package Services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = System.getProperty("db.url", "jdbc:sqlite:urzadskarbowy.db");
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initDatabase() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Podatnik (id TEXT PRIMARY KEY, imie TEXT NOT NULL, nazwisko TEXT NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Pracownik (id TEXT PRIMARY KEY, imie TEXT NOT NULL, nazwisko TEXT NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Mandat (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, kwota REAL NOT NULL, oplacony INTEGER NOT NULL, dataWystawienia TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id))");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Wizyta (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, data TEXT NOT NULL, pracownikId TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id), FOREIGN KEY(pracownikId) REFERENCES Pracownik(id))");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Wniosek (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, tresc TEXT NOT NULL, status TEXT NOT NULL, dataZlozenia TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id))");
        }
    }
}
