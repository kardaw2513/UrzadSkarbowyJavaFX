package Services;

import Model.Wizyta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WizytaService {
    private final Map<String, Wizyta> repo = new HashMap<>();

    public Wizyta umowWizyte(String podatnikId, LocalDate data, String pracownikId) {
        Wizyta w = new Wizyta(podatnikId, data, pracownikId);
        repo.put(w.getId(), w);
        return w;
    }

    public List<Wizyta> wszystkieWizyty() {
        return new ArrayList<>(repo.values());
    }

    public List<Wizyta> wizytyDlaPodatnika(String podatnikId) {
        return repo.values().stream()
                .filter(w -> w.getPodatnikId().equals(podatnikId))
                .collect(Collectors.toList());
    }
}