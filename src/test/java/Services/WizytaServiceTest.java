package Services;

import Model.Wizyta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WizytaServiceTest {
    private WizytaService wizytaService;
    private final String pid = "p3";
    private final String pracId = "prac1";

    @BeforeEach
    void setUp() {
        wizytaService = new WizytaService();
    }

    @Test
    void umowWizyte_i_wizytyDlaPodatnika() {
        Wizyta w = wizytaService.umowWizyte(pid, LocalDate.now(), pracId);
        List<Wizyta> lista = wizytaService.wizytyDlaPodatnika(pid);
        assertEquals(1, lista.size());
        assertEquals(pracId, lista.get(0).getPracownikId());
    }

    @Test
    void wszystkieWizyty_zwracaWszystkie() {
        wizytaService.umowWizyte(pid, LocalDate.now(), pracId);
        wizytaService.umowWizyte("inny", LocalDate.now(), pracId);
        List<Wizyta> wszystkie = wizytaService.wszystkieWizyty();
        assertEquals(2, wszystkie.size());
    }
}