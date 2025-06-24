package Services;

import Model.Wniosek;
import Services.dao.WniosekDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class WniosekService {
    private final WniosekDAO dao = new WniosekDAO();

    public Wniosek zlozWniosek(String podatnikId, String tresc) throws SQLException {
        Wniosek w = new Wniosek(podatnikId, tresc);
        dao.save(w);
        return w;
    }

    public Optional<Wniosek> pobierzWniosek(String id) throws SQLException {
        return dao.findById(id);
    }

    public List<Wniosek> wszystkieWnioski() throws SQLException {
        return dao.findAll();
    }

    public void zmienStatus(String id, Wniosek.Status status) throws SQLException {
        dao.updateStatus(id, status);
    }
}
