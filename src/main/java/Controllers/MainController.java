package Controllers;

import Services.CurrentSession;
import Services.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private Tab podatnicyTab;
    @FXML private Tab pracownicyTab;
    @FXML private Tab mandatyTab;
    @FXML private Tab wizytyTab;
    @FXML private Tab wnioskiTab;
    @FXML private Tab raportyTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("[MainController] initialize - initDatabase");
        try {
            DatabaseManager.initDatabase();
        } catch (SQLException e) {
            System.err.println("[MainController] initDatabase error: " + e.getMessage());
            e.printStackTrace();
        }

        // Tu: w zależności od roli CurrentSession, wyłączamy lub usuwamy zakładki:
        if (CurrentSession.isPodatnik()) {
            // Podatnik nie może zobaczyć listy wszystkich podatników ani pracowników,
            // ani wystawiać mandatów (zakładka Mandaty powinna pozwalać tylko opłacanie).
            // Można np. usunąć zakładki, których nie powinien widzieć:
            if (podatnicyTab != null) tabPane.getTabs().remove(podatnicyTab);
            if (pracownicyTab != null) tabPane.getTabs().remove(pracownicyTab);
            // Mandaty: pozostawiamy zakładkę, ale w jej controllerze ukryj przycisk "Wystaw mandat".
            // Tu ewentualnie można dodać flagę do MandatController (sprawdzi CurrentSession.isPodatnik()).
            // Wnioski: Podatnik może składać własne wnioski, więc zostawiamy zakładkę Wnioski, ale w tabeli widzi tylko swoje.
            // Wizyty: Podatnik może umawiać wizyty tylko dla siebie - w kontrolerze WizytaController ograniczamy listę do currentUserId.
            // Raporty: Podatnik widzi tylko swoje rekordy (w RaportController należy sprawdzić CurrentSession.isPodatnik()).
        }
        else if (CurrentSession.isPracownik()) {
            // Pracownik widzi wszystkie zakładki: podatnicy, pracownicy, mandaty, wizyty, wnioski, raporty.
            // W MandatController: może wystawiać i opłacać.
            // W WniosekController: tylko zmiana statusu (ale składać wnioski nie musi).
            // Można zostawić wszystko.
        }
        else {
            // Brak zalogowanego: e.g. przed wejściem do głównego widoku powinien być ekran logowania.
            // Jeśli jednak tu, to można np. zablokować wszystkie lub przenieść do logowania.
            // Na razie można usunąć wszystkie zakładki:
            tabPane.getTabs().clear();
        }
    }
}
