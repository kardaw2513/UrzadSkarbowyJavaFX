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
        System.setProperty("db.url", "jdbc:sqlite:file:memdb1?mode=memory&cache=shared");
        DatabaseManager.closeConnection();
        DatabaseManager.initDatabase();

        wizytaService = new WizytaService();
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.closeConnection();
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
