package Services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:urzad_skarbowy.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        System.out.println("[DatabaseManager] getConnection() – user.dir = " + System.getProperty("user.dir"));
        if (connection == null || connection.isClosed()) {
            System.out.println("[DatabaseManager] Tworzę nowe połączenie do: " + DB_URL);
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("[DatabaseManager] Nowa instancja Connection, hash=" + connection.hashCode());
        } else {
            System.out.println("[DatabaseManager] Reużywam istniejące połączenie, hash=" + connection.hashCode());
        }
        return connection;
    }

    public static void initDatabase() throws SQLException {
        System.out.println("[DatabaseManager] initDatabase: start");
        Connection conn = getConnection();

        String[] createStmts = {
                "CREATE TABLE IF NOT EXISTS Podatnik (id TEXT PRIMARY KEY, imie TEXT NOT NULL, nazwisko TEXT NOT NULL)",
                "CREATE TABLE IF NOT EXISTS Pracownik (id TEXT PRIMARY KEY, imie TEXT NOT NULL, nazwisko TEXT NOT NULL)",
                "CREATE TABLE IF NOT EXISTS Mandat (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, kwota REAL NOT NULL, oplacony INTEGER NOT NULL, dataWystawienia TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id))",
                "CREATE TABLE IF NOT EXISTS Wizyta (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, data TEXT NOT NULL, pracownikId TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id), FOREIGN KEY(pracownikId) REFERENCES Pracownik(id))",
                "CREATE TABLE IF NOT EXISTS Wniosek (id TEXT PRIMARY KEY, podatnikId TEXT NOT NULL, tresc TEXT NOT NULL, status TEXT NOT NULL, dataZlozenia TEXT NOT NULL, FOREIGN KEY(podatnikId) REFERENCES Podatnik(id))"
        };

        for (String sql : createStmts) {
            System.out.println("[DatabaseManager] Wykonuję: " + sql);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
                String tableName = extractTableName(sql);
                if (tableName != null) {
                    boolean exists = checkTableExists(conn, tableName);
                    System.out.println("[DatabaseManager]  -> tabela '" + tableName + "' istnieje? " + exists);
                }
            } catch (SQLException e) {
                System.err.println("[DatabaseManager] Błąd przy: " + sql + " -> " + e.getMessage());
            }
        }

        // Teraz listujemy wszystkie tabele i ich zawartość, używając oddzielnych Statement
        System.out.println("[DatabaseManager] >>> Lista tabel i ich zawartość po initDatabase:");
        try (Statement stmtTables = conn.createStatement()) {
            ResultSet tablesRs = stmtTables.executeQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name");
            while (tablesRs.next()) {
                String tableName = tablesRs.getString("name");
                System.out.println("[DatabaseManager] Tabela: " + tableName);

                // osobny Statement dla wierszy
                try (Statement stmtRows = conn.createStatement();
                     ResultSet rows = stmtRows.executeQuery("SELECT * FROM \"" + tableName + "\"")) {
                    ResultSetMetaData meta = rows.getMetaData();
                    int columnCount = meta.getColumnCount();
                    boolean hasRows = false;
                    while (rows.next()) {
                        hasRows = true;
                        StringBuilder row = new StringBuilder("  ");
                        for (int i = 1; i <= columnCount; i++) {
                            String colName = meta.getColumnName(i);
                            String value = rows.getString(i);
                            row.append(colName).append("=").append(value).append(" ");
                        }
                        System.out.println(row);
                    }
                    if (!hasRows) {
                        System.out.println("  <pusta tabela>");
                    }
                } catch (SQLException e) {
                    System.err.println("[DatabaseManager] Błąd czytania tabeli " + tableName + ": " + e.getMessage());
                }
            }
            tablesRs.close();
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Błąd listowania tabel: " + e.getMessage());
        }

        System.out.println("[DatabaseManager] initDatabase: koniec");
    }

    private static String extractTableName(String createSql) {
        String up = createSql.toUpperCase();
        String marker = "CREATE TABLE IF NOT EXISTS ";
        int idx = up.indexOf(marker);
        if (idx >= 0) {
            String rest = createSql.substring(idx + marker.length()).trim();
            int space = rest.indexOf(' ');
            int paren = rest.indexOf('(');
            int end = space > 0 ? space : (paren > 0 ? paren : rest.length());
            return rest.substring(0, end).replace("\"", "").replace("`", "");
        }
        return null;
    }

    private static boolean checkTableExists(Connection conn, String tableName) {
        try (Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'");
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] checkTableExists error dla " + tableName + ": " + e.getMessage());
            return false;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                System.out.println("[DatabaseManager] closeConnection: zamykam połączenie, hash=" + connection.hashCode());
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }
}
