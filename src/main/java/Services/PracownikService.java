package Services;

import Model.Pracownik;
import Services.dao.PracownikDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PracownikService {
    private final PracownikDAO dao = new PracownikDAO();

    public Pracownik dodajPracownika(String imie, String nazwisko) throws SQLException {
        Pracownik p = new Pracownik(imie, nazwisko);
        dao.save(p);
        return p;
    }

    public Optional<Pracownik> pobierzPracownika(String id) throws SQLException {
        return dao.findById(id);
    }

    public List<Pracownik> wszyscyPracownicy() throws SQLException {
        return dao.findAll();
    }
}
