package Model;

import java.util.UUID;

public class Pracownik {
    private final String id;
    private final String imie;
    private final String nazwisko;

    public Pracownik(String imie, String nazwisko) {
        this.id = UUID.randomUUID().toString();
        this.imie = imie;
        this.nazwisko = nazwisko;
    }

    // Konstruktor z ID
    public Pracownik(String id, String imie, String nazwisko) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
    }

    public String getId() {
        return id;
    }
    public String getImie() {
        return imie;
    }
    public String getNazwisko() {
        return nazwisko;
    }

    @Override
    public String toString() {
        return imie + " " + nazwisko;
    }
}
