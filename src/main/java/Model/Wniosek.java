package Model;

import java.time.LocalDate;
import java.util.UUID;

public class Wniosek {
    public enum Status { ZLOZONY, W_TRAKCIE, ROZPATRZONY }

    private final String id;
    private final String podatnikId;
    private final String tresc;
    private Status status;
    private final LocalDate dataZlozenia;

    public Wniosek(String podatnikId, String tresc) {
        this.id = UUID.randomUUID().toString();
        this.podatnikId = podatnikId;
        this.tresc = tresc;
        this.status = Status.ZLOZONY;
        this.dataZlozenia = LocalDate.now();
    }

    // Konstruktor z wszystkimi polami
    public Wniosek(String id, String podatnikId, String tresc, Status status, LocalDate dataZlozenia) {
        this.id = id;
        this.podatnikId = podatnikId;
        this.tresc = tresc;
        this.status = status;
        this.dataZlozenia = dataZlozenia;
    }

    public String getId() {
        return id;
    }
    public String getPodatnikId() {
        return podatnikId;
    }
    public String getTresc() {
        return tresc;
    }
    public Status getStatus() {
        return status;
    }
    public LocalDate getDataZlozenia() {
        return dataZlozenia;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
