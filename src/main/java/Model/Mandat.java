package Model;

import java.time.LocalDate;
import java.util.UUID;

public class Mandat {
    private final String id;
    private final String podatnikId;
    private final double kwota;
    private boolean oplacony;
    private final LocalDate dataWystawienia;

    public Mandat(String podatnikId, double kwota) {
        this.id = UUID.randomUUID().toString();
        this.podatnikId = podatnikId;
        this.kwota = kwota;
        this.oplacony = false;
        this.dataWystawienia = LocalDate.now();
    }

    public String getId() { return id; }
    public String getPodatnikId() { return podatnikId; }
    public double getKwota() { return kwota; }
    public boolean isOplacony() { return oplacony; }
    public LocalDate getDataWystawienia() { return dataWystawienia; }

    public void zaplac() {
        this.oplacony = true;
    }
}