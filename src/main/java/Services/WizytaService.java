package Services;

import Model.Wizyta;
import Services.dao.WizytaDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class WizytaService {
    private final WizytaDAO dao = new WizytaDAO();

    public Wizyta umowWizyte(String podatnikId, LocalDate data, String pracownikId) throws SQLException {
        Wizyta w = new Wizyta(podatnikId, data, pracownikId);
        dao.save(w);
        return w;
    }

    public List<Wizyta> wszystkieWizyty() throws SQLException {
        return dao.findAll();
    }

    public List<Wizyta> wizytyDlaPodatnika(String podatnikId) throws SQLException {
        return dao.findByPodatnik(podatnikId);
    }

    // Jeśli potrzebujesz update/delete, dodaj w DAO i tu odpowiednie metody
}
