package Services;

import Model.Mandat;
import Model.Wizyta;
import Model.Wniosek;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RaportServiceTest {
    private MandatService ms;
    private WniosekService ws;
    private WizytaService vs;
    private RaportService rs;

    // stany początkowe
    private int mandatyTotalBefore;
    private int mandatyPaidBefore;
    private int wizytyBefore;
    private Map<Wniosek.Status, Integer> wnioskiBefore;

    @BeforeAll
    void initAll() throws SQLException {
        DatabaseManager.initDatabase();
        ms = new MandatService();
        ws = new WniosekService();
        vs = new WizytaService();
        rs = new RaportService(ms, ws, vs);
    }

    @BeforeEach
    void captureBefore() throws SQLException {
        // przed każdym testem odczytujemy bieżące stany
        mandatyTotalBefore = ms.wszystkieMandaty().size();
        mandatyPaidBefore = (int) ms.wszystkieMandaty().stream().filter(Mandat::isOplacony).count();
        wizytyBefore = vs.wszystkieWizyty().size();
        wnioskiBefore = new EnumMap<>(Wniosek.Status.class);
        for (Wniosek.Status status : Wniosek.Status.values()) {
            int count = (int) ws.wszystkieWnioski().stream().filter(w -> w.getStatus() == status).count();
            wnioskiBefore.put(status, count);
        }
    }

    @Test
    void raportMandatow_filtrowanieOplaconych() throws SQLException {
        String pid = UUID.randomUUID().toString();
        // dodajemy 2 mandaty, jeden opłacamy
        Mandat m1 = ms.wystawMandat(pid, 100);
        Mandat m2 = ms.wystawMandat(pid, 200);
        ms.zaplacMandat(m1.getId());

        // po dodaniu: całkowita liczba mandatóww wzrosła o 2
        List<Mandat> wszystkie = rs.raportMandatow(false);
        assertEquals(mandatyTotalBefore + 2, wszystkie.size(),
                "Po dodaniu 2 mandatów raportMandatow(false) powinien zwrócić wcześniejsze +2");

        // liczba opłaconych wzrosła o 1
        List<Mandat> oplacone = rs.raportMandatow(true);
        assertEquals(mandatyPaidBefore + 1, oplacone.size(),
                "Po opłaceniu 1 mandatu raportMandatow(true) powinien zwrócić wcześniejsze +1");
    }

    @Test
    void raportWizyt_zawieraWszystkie() throws SQLException {
        String pid = UUID.randomUUID().toString();
        String pracId = UUID.randomUUID().toString();
        // dodajemy 2 wizyty
        vs.umowWizyte(pid, LocalDate.now(), pracId);
        vs.umowWizyte(pid, LocalDate.now().plusDays(1), pracId);

        List<Wizyta> wizyty = rs.raportWizyt();
        assertEquals(wizytyBefore + 2, wizyty.size(),
                "Po dodaniu 2 wizyt raportWizyt powinien zwrócić wcześniejsze +2");
    }

    @Test
    void raportWnioskow_filtrowaniePoStatusie() throws SQLException {
        String pid = UUID.randomUUID().toString();
        // Dodajemy wnioski w różnych statusach
        Wniosek w1 = ws.zlozWniosek(pid, "A");
        Wniosek w2 = ws.zlozWniosek(pid, "B");
        // zmieniamy status drugiego
        ws.zmienStatus(w2.getId(), Wniosek.Status.ROZPATRZONY);

        // raport dla ZLOZONY: przed było wnioskiBefore.get(ZLOZONY), teraz dodaliśmy 1 nowy w statusie ZLOZONY
        List<Wniosek> zlozone = rs.raportWnioskow(Wniosek.Status.ZLOZONY);
        assertEquals(wnioskiBefore.get(Wniosek.Status.ZLOZONY) + 1, zlozone.size(),
                "Po dodaniu 1 wniosku w statusie ZLOZONY liczba w raporcie powinna wzrosnąć o 1");

        // raport dla ROZPATRZONY: przed było wnioskiBefore.get(ROZPATRZONY), teraz dodaliśmy 1
        List<Wniosek> rozpatrzone = rs.raportWnioskow(Wniosek.Status.ROZPATRZONY);
        assertEquals(wnioskiBefore.get(Wniosek.Status.ROZPATRZONY) + 1, rozpatrzone.size(),
                "Po dodaniu 1 wniosku w statusie ROZPATRZONY liczba w raporcie powinna wzrosnąć o 1");
    }
}
