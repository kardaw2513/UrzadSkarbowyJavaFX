package Services;

import Model.Wniosek;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WniosekServiceTest {
    private WniosekService wniosekService;
    private final String pid = "p2";

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetDatabaseManagerConnection();
        wniosekService = new WniosekService();
    }

//    @AfterEach
//    void tearDown() {
//        TestUtils.closeDatabaseManagerConnection();
//    }

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
