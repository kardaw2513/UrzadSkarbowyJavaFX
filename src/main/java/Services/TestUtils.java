package Services;

import java.lang.reflect.Field;
import java.sql.Connection;

public class TestUtils {
    public static void resetDatabaseManagerConnection() throws Exception {
        // ustaw in-memory URL
        System.out.println("[TestUtils] resetDatabaseManagerConnection: ustawiam db.url=jdbc:sqlite::memory:");
        System.setProperty("db.url", "jdbc:sqlite::memory:");
        // zamknij stare połączenie
        Field connField = DatabaseManager.class.getDeclaredField("connection");
        connField.setAccessible(true);
        Connection conn = (Connection) connField.get(null);
        if (conn != null && !conn.isClosed()) {
            System.out.println("[TestUtils] reset: zamykam stare połączenie");
            conn.close();
        }
        connField.set(null, null);
        // inicjalizuj tabele w nowej in-memory bazie
        DatabaseManager.initDatabase();
        System.out.println("[TestUtils] reset: initDatabase wykonane");
    }

    public static void closeDatabaseManagerConnection() {
        System.out.println("[TestUtils] closeDatabaseManagerConnection: zamykam połączenie");
        DatabaseManager.closeConnection();
    }
}
