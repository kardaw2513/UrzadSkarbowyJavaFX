package Services;

import Model.Wizyta;
import org.junit.jupiter.api.AfterEach;
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
        System.out.println("[WizytaServiceTest] @BeforeEach - reset bazy");
        TestUtils.resetDatabaseManagerConnection();
        wizytaService = new WizytaService();
    }

//    @AfterEach
//    void tearDown() {
//        System.out.println("[WizytaServiceTest] @AfterEach - zamykam połączenie");
//        TestUtils.closeDatabaseManagerConnection();
//    }

    @Test
    void umowWizyte_i_wizytyDlaPodatnika() throws SQLException {
        System.out.println("[WizytaServiceTest] test umowWizyte_i_wizytyDlaPodatnika");
        Wizyta w = wizytaService.umowWizyte(pid, LocalDate.now(), pracId);
        List<Wizyta> lista = wizytaService.wizytyDlaPodatnika(pid);
        assertEquals(1, lista.size());
        assertEquals(pracId, lista.get(0).getPracownikId());
        assertEquals(w.getId(), lista.get(0).getId());
    }

    @Test
    void wszystkieWizyty_zwracaWszystkie() throws SQLException {
        System.out.println("[WizytaServiceTest] test wszystkieWizyty_zwracaWszystkie");
        wizytaService.umowWizyte(pid, LocalDate.now(), pracId);
        wizytaService.umowWizyte("inny", LocalDate.now().plusDays(1), pracId);
        List<Wizyta> wszystkie = wizytaService.wszystkieWizyty();
        assertEquals(2, wszystkie.size());
    }
}
