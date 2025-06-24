package Services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // URL bazy; domyślnie plik urzadskarbowy.db, można nadpisać przez -Ddb.url=...
    private static final String DB_URL = System.getProperty("db.url", "jdbc:sqlite:urzadskarbowy.db");
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("[DatabaseManager] Otwieram nowe połączenie do: " + DB_URL);
            connection = DriverManager.getConnection(DB_URL);
        } else {
            System.out.println("[DatabaseManager] Reużywam istniejące połączenie do: " + DB_URL);
        }
        return connection;
    }

    public static void initDatabase() throws SQLException {
        System.out.println("[DatabaseManager] initDatabase: tworzenie tabel jeśli nie istnieją...");
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            // Przykładowe CREATE TABLE z debugiem
            String sql;
            sql = "CREATE TABLE IF NOT EXISTS Podatnik (id TEXT PRIMARY KEY, imie TEXT NOT NULL, nazwisko TEXT NOT NULL)";
            System.out.println("[DatabaseManager] SQL: " + sql);
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Pracownik (id TEXT PRIMARY KEY, imie TEXT NOT NULL, nazwisko TEXT NOT NULL)";
            System.out.println("[DatabaseManager] SQL: " + sql);
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Mandat (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, kwota REAL NOT NULL, oplacony INTEGER NOT NULL, dataWystawienia TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id))";
            System.out.println("[DatabaseManager] SQL: " + sql);
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Wizyta (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, data TEXT NOT NULL, pracownikId TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id), FOREIGN KEY(pracownikId) REFERENCES Pracownik(id))";
            System.out.println("[DatabaseManager] SQL: " + sql);
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS Wniosek (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, tresc TEXT NOT NULL, status TEXT NOT NULL, dataZlozenia TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id))";
            System.out.println("[DatabaseManager] SQL: " + sql);
            stmt.executeUpdate(sql);
        }
        // Nie zamykamy connection: przy in-memory SQLite tabele są zachowywane dopóki połączenie otwarte
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                System.out.println("[DatabaseManager] closeConnection: zamykam połączenie");
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }
}
