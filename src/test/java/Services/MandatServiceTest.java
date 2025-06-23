package Services;

import Model.Mandat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MandatServiceTest {
    private MandatService service;
    private String pid = "p1";

    @BeforeEach
    void setUp() {
        service = new MandatService();
    }

    @Test
    void wystawMandat_i_pokazMandaty() {
        Mandat m = service.wystawMandat(pid, 100.0);
        List<Mandat> lista = service.pokazMandaty(pid);
        assertEquals(1, lista.size());
        assertEquals(100.0, lista.get(0).getKwota());
    }

    @Test
    void zaplacMandat_ustawiaOplacony() {
        Mandat m = service.wystawMandat(pid, 50.0);
        service.zaplacMandat(m.getId());
        assertTrue(service.pokazMandaty(pid).get(0).isOplacony());
    }
}
