package Services;

import Model.Wizyta;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WizytaServiceTest {
    private WizytaService wizytaService;

    @BeforeAll
    void init() throws SQLException {
        DatabaseManager.initDatabase();
        wizytaService = new WizytaService();
    }

    @Test
    void umowWizyte_zwiększaLiczbęWizytDlaPodatnika() throws SQLException {
        String pid = UUID.randomUUID().toString();
        String pracId = UUID.randomUUID().toString();
        // stan początkowy wizyt dla tego podatnika
        List<Wizyta> beforeList = wizytaService.wizytyDlaPodatnika(pid);
        int before = beforeList.size();

        Wizyta w = wizytaService.umowWizyte(pid, LocalDate.now().plusDays(1), pracId);
        assertNotNull(w);
        List<Wizyta> afterList = wizytaService.wizytyDlaPodatnika(pid);
        assertEquals(before + 1, afterList.size(), "Po umówieniu wizyty liczba wizyt dla podatnika powinna wzrosnąć o 1");
        Wizyta found = afterList.stream().filter(x -> x.getId().equals(w.getId())).findFirst().orElse(null);
        assertNotNull(found);
        assertEquals(pracId, found.getPracownikId());
    }

    @Test
    void wszystkieWizyty_zwracaPoprawnąLiczbęPoDodaniu() throws SQLException {
        // ogólna liczba wizyt przed dodaniem
        List<Wizyta> beforeAll = wizytaService.wszystkieWizyty();
        int before = beforeAll.size();

        String pidA = UUID.randomUUID().toString();
        String pidB = UUID.randomUUID().toString();
        String pracId = UUID.randomUUID().toString();
        wizytaService.umowWizyte(pidA, LocalDate.now().plusDays(2), pracId);
        wizytaService.umowWizyte(pidB, LocalDate.now().plusDays(3), pracId);

        List<Wizyta> afterAll = wizytaService.wszystkieWizyty();
        assertEquals(before + 2, afterAll.size(), "Po dodaniu 2 wizyt ogólna liczba powinna wzrosnąć o 2");
    }
}
