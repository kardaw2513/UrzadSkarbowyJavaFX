package Model;

import java.util.UUID;

public class Podatnik {
    private final String id;
    private final String imie;
    private final String nazwisko;

    public Podatnik(String imie, String nazwisko) {
        this.id = UUID.randomUUID().toString();
        this.imie = imie;
        this.nazwisko = nazwisko;
    }
    // nowy:
    public Podatnik(String id, String imie, String nazwisko) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
    }
    public String getId() { return id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
}
