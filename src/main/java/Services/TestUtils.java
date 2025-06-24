// src/test/java/Services/TestUtils.java
package Services;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public class TestUtils {
    public static void resetDatabaseManagerConnection() throws Exception {
        // Ustaw in-memory URL
        System.setProperty("db.url", "jdbc:sqlite::memory:");
        // Uzyskaj i zamknij stare połączenie, jeśli istnieje
        Field connField = DatabaseManager.class.getDeclaredField("connection");
        connField.setAccessible(true);
        Connection conn = (Connection) connField.get(null);
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
        // Ustaw pole na null, by next getConnection otworzyło nowe
        connField.set(null, null);
        // Inicjalizuj schemat
        DatabaseManager.initDatabase();
    }
}
