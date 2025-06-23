package Services;

import Model.Wniosek;

import java.util.*;

public class WniosekService {
    private final Map<String, Wniosek> repo = new HashMap<>();

    public Wniosek zlozWniosek(String podatnikId, String tresc) {
        Wniosek w = new Wniosek(podatnikId, tresc);
        repo.put(w.getId(), w);
        return w;
    }

    public Optional<Wniosek> pobierzWniosek(String id) {
        return Optional.ofNullable(repo.get(id));
    }

    public List<Wniosek> wszystkieWnioski() {
        return new ArrayList<>(repo.values());
    }

    public void zmienStatus(String id, Wniosek.Status status) {
        Wniosek w = repo.get(id);
        if (w != null) w.setStatus(status);
    }
}