package Services;

import Model.Mandat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MandatServiceTest {
    private MandatService service;

    @BeforeAll
    void init() throws SQLException {
        // Inicjalizacja bazy i serwisu raz przed wszystkimi testami
        DatabaseManager.initDatabase();
        service = new MandatService();
    }

    @Test
    void wystawMandat_zwiększaLiczbęMandatówDlaPodatnika() throws SQLException {
        String pid = UUID.randomUUID().toString(); // unikalny, by nie kolidować z istniejącymi
        // stan początkowy dla tego podatnika:
        List<Mandat> beforeList = service.pokazMandaty(pid);
        int before = beforeList.size();

        Mandat m = service.wystawMandat(pid, 123.45);
        assertNotNull(m);
        // po dodaniu:
        List<Mandat> afterList = service.pokazMandaty(pid);
        assertEquals(before + 1, afterList.size(), "Po wystawieniu mandatu liczba powinna wzrosnąć o 1");
        // sprawdź dane nowego mandatu:
        Mandat found = afterList.stream().filter(x -> x.getId().equals(m.getId())).findFirst().orElse(null);
        assertNotNull(found);
        assertEquals(123.45, found.getKwota());
        assertFalse(found.isOplacony());
    }

    @Test
    void zaplacMandat_ustawiaOplacony() throws SQLException {
        String pid = UUID.randomUUID().toString();
        Mandat m = service.wystawMandat(pid, 50.0);
        // początkowo nieopłacony
        assertFalse(service.pokazMandaty(pid).stream()
                .filter(x -> x.getId().equals(m.getId())).findFirst().get().isOplacony());

        service.zaplacMandat(m.getId());
        // po zapłacie:
        Mandat after = service.pokazMandaty(pid).stream()
                .filter(x -> x.getId().equals(m.getId())).findFirst().orElse(null);
        assertNotNull(after);
        assertTrue(after.isOplacony(), "Mandat powinien być oznaczony jako opłacony");
    }

    @Test
    void wszystkieMandaty_zwracaPoprawnąLiczbęPoDodaniu() throws SQLException {
        // stan początkowy ogólnej liczby mandatóww
        List<Mandat> beforeAll = service.wszystkieMandaty();
        int before = beforeAll.size();

        // dodajemy mandaty dla unikalnych podatników
        String pid1 = UUID.randomUUID().toString();
        String pid2 = UUID.randomUUID().toString();
        service.wystawMandat(pid1, 10.0);
        service.wystawMandat(pid2, 20.0);

        List<Mandat> afterAll = service.wszystkieMandaty();
        assertEquals(before + 2, afterAll.size(), "Po dodaniu 2 nowych mandatów suma wszystkich powinna wzrosnąć o 2");
    }
}
