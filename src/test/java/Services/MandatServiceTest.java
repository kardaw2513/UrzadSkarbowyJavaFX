package Services;

import Model.Mandat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MandatServiceTest {
    private MandatService service;
    private final String pid = "p1";

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.resetDatabaseManagerConnection();
        service = new MandatService();
    }

//    @AfterEach
//    void tearDown() {
//        TestUtils.closeDatabaseManagerConnection();
//    }

    @Test
    void wystawMandat_i_pokazMandaty() throws SQLException {
        Mandat m = service.wystawMandat(pid, 100.0);
        List<Mandat> lista = service.pokazMandaty(pid);
        assertEquals(1, lista.size());
        assertEquals(100.0, lista.get(0).getKwota());
        assertFalse(lista.get(0).isOplacony());
        assertEquals(m.getId(), lista.get(0).getId());
    }

    @Test
    void zaplacMandat_ustawiaOplacony() throws SQLException {
        Mandat m = service.wystawMandat(pid, 50.0);
        service.zaplacMandat(m.getId());
        List<Mandat> lista = service.pokazMandaty(pid);
        assertTrue(lista.get(0).isOplacony());
    }

    @Test
    void zaplacMandat_nonexistent_noException() throws SQLException {
        service.zaplacMandat("nieistnieje");
        List<Mandat> lista = service.pokazMandaty(pid);
        assertTrue(lista.isEmpty());
    }

    @Test
    void wszystkieMandaty_zwracaWszystkie() throws SQLException {
        service.wystawMandat(pid, 10);
        service.wystawMandat("inny", 20);
        List<Mandat> wszystkie = service.wszystkieMandaty();
        assertEquals(2, wszystkie.size());
    }
}
