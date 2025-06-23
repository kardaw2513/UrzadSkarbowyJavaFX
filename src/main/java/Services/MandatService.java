package Services;

import Model.Mandat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MandatService {
    private final Map<String, Mandat> repo = new HashMap<>();

    public Mandat wystawMandat(String podatnikId, double kwota) {
        Mandat m = new Mandat(podatnikId, kwota);
        repo.put(m.getId(), m);
        return m;
    }

    public void zaplacMandat(String mandatId) {
        Mandat m = repo.get(mandatId);
        if (m != null) m.zaplac();
    }

    public List<Mandat> pokazMandaty(String podatnikId) {
        return repo.values().stream()
                .filter(m -> m.getPodatnikId().equals(podatnikId))
                .collect(Collectors.toList());
    }

    public List<Mandat> wszystkieMandaty() {
        return new ArrayList<>(repo.values());
    }
}