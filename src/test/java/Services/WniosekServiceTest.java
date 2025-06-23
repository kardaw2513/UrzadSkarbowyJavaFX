package Services;

import Model.Wniosek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WniosekServiceTest {
    private WniosekService wniosekService;
    private final String pid = "p2";

    @BeforeEach
    void setUp() {
        wniosekService = new WniosekService();
    }

    @Test
    void zlozWniosek_i_pobierzWniosek() {
        Wniosek w = wniosekService.zlozWniosek(pid, "Test treść");
        Optional<Wniosek> opt = wniosekService.pobierzWniosek(w.getId());
        assertTrue(opt.isPresent());
        assertEquals("Test treść", opt.get().getTresc());
    }

    @Test
    void zmienStatus_dzialaPoprawnie() {
        Wniosek w = wniosekService.zlozWniosek(pid, "Treść");
        wniosekService.zmienStatus(w.getId(), Wniosek.Status.W_TRAKCIE);
        Optional<Wniosek> opt = wniosekService.pobierzWniosek(w.getId());
        assertTrue(opt.isPresent());
        assertEquals(Wniosek.Status.W_TRAKCIE, opt.get().getStatus());
    }
}