package Model;

import java.time.LocalDate;
import java.util.UUID;

public class Wizyta {
    private final String id;
    private final String podatnikId;
    private final LocalDate data;
    private final String pracownikId;

    public Wizyta(String podatnikId, LocalDate data, String pracownikId) {
        this.id = UUID.randomUUID().toString();
        this.podatnikId = podatnikId;
        this.data = data;
        this.pracownikId = pracownikId;
    }

    // Konstruktor z ID (przy odczycie z bazy)
    public Wizyta(String id, String podatnikId, LocalDate data, String pracownikId) {
        this.id = id;
        this.podatnikId = podatnikId;
        this.data = data;
        this.pracownikId = pracownikId;
    }

    public String getId() {
        return id;
    }
    public String getPodatnikId() {
        return podatnikId;
    }
    public LocalDate getData() {
        return data;
    }
    public String getPracownikId() {
        return pracownikId;
    }
}
