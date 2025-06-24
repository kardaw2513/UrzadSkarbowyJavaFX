package Services;

import Model.Wniosek;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WniosekServiceTest {
    private WniosekService wniosekService;

    @BeforeAll
    void init() throws SQLException {
        DatabaseManager.initDatabase();
        wniosekService = new WniosekService();
    }

    @Test
    void zlozWniosek_i_pobierzWniosek() throws SQLException {
        String pid = UUID.randomUUID().toString();
        List<Wniosek> beforeList = wniosekService.wszystkieWnioski();
        int before = beforeList.size();

        Wniosek w = wniosekService.zlozWniosek(pid, "Test treść");
        assertNotNull(w);
        Optional<Wniosek> opt = wniosekService.pobierzWniosek(w.getId());
        assertTrue(opt.isPresent());
        assertEquals("Test treść", opt.get().getTresc());
        assertEquals(Wniosek.Status.ZLOZONY, opt.get().getStatus());

        // liczba wszystkich wniosków wzrosła o 1
        List<Wniosek> afterList = wniosekService.wszystkieWnioski();
        assertEquals(before + 1, afterList.size(), "Po złożeniu wniosku liczba wszystkich wniosków powinna wzrosnąć o 1");
    }

    @Test
    void zmienStatus_zmieniaStatusPoprawnie() throws SQLException {
        String pid = UUID.randomUUID().toString();
        Wniosek w = wniosekService.zlozWniosek(pid, "Treść do zmiany");
        // przed zmianą status ZLOZONY
        assertEquals(Wniosek.Status.ZLOZONY, w.getStatus());

        wniosekService.zmienStatus(w.getId(), Wniosek.Status.W_TRAKCIE);
        Optional<Wniosek> opt = wniosekService.pobierzWniosek(w.getId());
        assertTrue(opt.isPresent());
        assertEquals(Wniosek.Status.W_TRAKCIE, opt.get().getStatus(), "Status powinien się zmienić na W_TRAKCIE");
    }

    @Test
    void wszystkieWnioski_zwracaPoprawnąLiczbęPoDodaniu() throws SQLException {
        int before = wniosekService.wszystkieWnioski().size();
        String pidA = UUID.randomUUID().toString();
        String pidB = UUID.randomUUID().toString();
        wniosekService.zlozWniosek(pidA, "A");
        wniosekService.zlozWniosek(pidB, "B");
        List<Wniosek> after = wniosekService.wszystkieWnioski();
        assertEquals(before + 2, after.size(), "Po dodaniu 2 wniosków liczba wszystkich powinna wzrosnąć o 2");
    }
}
