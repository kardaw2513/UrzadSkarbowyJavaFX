package Services;

import Model.Podatnik;
import Services.dao.PodatnikDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PodatnikService {
    private final PodatnikDAO dao = new PodatnikDAO();

    public Podatnik dodajPodatnika(String imie, String nazwisko) throws SQLException {
        Podatnik p = new Podatnik(imie, nazwisko);
        dao.save(p);
        return p;
    }

    public Optional<Podatnik> pobierzPodatnika(String id) throws SQLException {
        return dao.findById(id);
    }

    public List<Podatnik> wszyscyPodatnicy() throws SQLException {
        return dao.findAll();
    }

    // Można dodać metody update/delete, jeśli DAO je wspiera
}
