// src/test/java/Services/WizytaServiceTest.java
package Services;

import Model.Wizyta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WizytaServiceTest {
    private WizytaService wizytaService;
    private final String pid = "p3";
    private final String pracId = "prac1";

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetDatabaseManagerConnection();
        // Dodaj wymagane Podatnik i Pracownik w bazie, bo FK może być nieegzekwowane przez SQLite domyślnie,
        // ale jeśli chcesz walidować istnienie, utwórz odpowiednie rekordy.
        // W tym przykładzie nie sprawdzamy istnienia, więc wystarczy serwis.
        wizytaService = new WizytaService();
    }

    @Test
    void umowWizyte_i_wizytyDlaPodatnika() throws SQLException {
        Wizyta w = wizytaService.umowWizyte(pid, LocalDate.now(), pracId);
        List<Wizyta> lista = wizytaService.wizytyDlaPodatnika(pid);
        assertEquals(1, lista.size());
        assertEquals(pracId, lista.get(0).getPracownikId());
        assertEquals(w.getId(), lista.get(0).getId());
    }

    @Test
    void wszystkieWizyty_zwracaWszystkie() throws SQLException {
        wizytaService.umowWizyte(pid, LocalDate.now(), pracId);
        wizytaService.umowWizyte("inny", LocalDate.now().plusDays(1), pracId);
        List<Wizyta> wszystkie = wizytaService.wszystkieWizyty();
        assertEquals(2, wszystkie.size());
    }
}
