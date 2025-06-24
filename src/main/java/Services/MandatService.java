package Services;

import Model.Mandat;
import Services.dao.MandatDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MandatService {
    private final MandatDAO dao = new MandatDAO();

    public Mandat wystawMandat(String podatnikId, double kwota) throws SQLException {
        Mandat m = new Mandat(podatnikId, kwota);
        dao.save(m);
        return m;
    }

    public void zaplacMandat(String mandatId) throws SQLException {
        Optional<Mandat> opt = dao.findById(mandatId);
        if (opt.isPresent()) {
            Mandat m = opt.get();
            if (!m.isOplacony()) {
                m.zaplac();
                dao.update(m);
            }
        }
    }

    public List<Mandat> pokazMandaty(String podatnikId) throws SQLException {
        return dao.findByPodatnik(podatnikId);
    }

    public List<Mandat> wszystkieMandaty() throws SQLException {
        return dao.findAll();
    }
}
