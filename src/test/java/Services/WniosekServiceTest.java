// src/test/java/Services/WniosekServiceTest.java
package Services;

import Model.Wniosek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WniosekServiceTest {
    private WniosekService wniosekService;
    private final String pid = "p2";

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetDatabaseManagerConnection();
        wniosekService = new WniosekService();
    }

    @Test
    void zlozWniosek_i_pobierzWniosek() throws SQLException {
        Wniosek w = wniosekService.zlozWniosek(pid, "Test treść");
        Optional<Wniosek> opt = wniosekService.pobierzWniosek(w.getId());
        assertTrue(opt.isPresent());
        assertEquals("Test treść", opt.get().getTresc());
        assertEquals(Wniosek.Status.ZLOZONY, opt.get().getStatus());
    }

    @Test
    void zmienStatus_dzialaPoprawnie() throws SQLException {
        Wniosek w = wniosekService.zlozWniosek(pid, "Treść");
        wniosekService.zmienStatus(w.getId(), Wniosek.Status.W_TRAKCIE);
        Optional<Wniosek> opt = wniosekService.pobierzWniosek(w.getId());
        assertTrue(opt.isPresent());
        assertEquals(Wniosek.Status.W_TRAKCIE, opt.get().getStatus());
    }

    @Test
    void wszystkieWnioski_zawieraDodane() throws SQLException {
        wniosekService.zlozWniosek(pid, "A");
        wniosekService.zlozWniosek(pid, "B");
        List<Wniosek> list = wniosekService.wszystkieWnioski();
        assertEquals(2, list.size());
    }
}
