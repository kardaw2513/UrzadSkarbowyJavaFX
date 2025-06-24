// src/test/java/Services/RaportServiceTest.java
package Services;

import Model.Mandat;
import Model.Wizyta;
import Model.Wniosek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RaportServiceTest {
    private MandatService ms;
    private WniosekService ws;
    private WizytaService vs;
    private RaportService rs;
    private final String pid = "p4";
    private final String pracId = "prac2";

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetDatabaseManagerConnection();
        ms = new MandatService();
        ws = new WniosekService();
        vs = new WizytaService();
        rs = new RaportService(ms, ws, vs);
    }

    @Test
    void raportMandatow_filtrowanieOplaconych() throws SQLException {
        Mandat m1 = ms.wystawMandat(pid, 100);
        Mandat m2 = ms.wystawMandat(pid, 200);
        ms.zaplacMandat(m1.getId());

        List<Mandat> wszystkie = rs.raportMandatow(false);
        assertEquals(2, wszystkie.size());

        List<Mandat> oplacone = rs.raportMandatow(true);
        assertEquals(1, oplacone.size());
        assertTrue(oplacone.get(0).isOplacony());
    }

    @Test
    void raportWizyt_zawieraWszystkie() throws SQLException {
        vs.umowWizyte(pid, LocalDate.now(), pracId);
        vs.umowWizyte(pid, LocalDate.now().plusDays(1), pracId);
        List<Wizyta> wizyty = rs.raportWizyt();
        assertEquals(2, wizyty.size());
    }

    @Test
    void raportWnioskow_filtrowaniePoStatusie() throws SQLException {
        Wniosek w1 = ws.zlozWniosek(pid, "A");
        Wniosek w2 = ws.zlozWniosek(pid, "B");
        ws.zmienStatus(w2.getId(), Wniosek.Status.ROZPATRZONY);

        List<Wniosek> zlozone = rs.raportWnioskow(Wniosek.Status.ZLOZONY);
        assertEquals(1, zlozone.size());

        List<Wniosek> rozpatrzone = rs.raportWnioskow(Wniosek.Status.ROZPATRZONY);
        assertEquals(1, rozpatrzone.size());
    }
}
