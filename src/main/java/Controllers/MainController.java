package Controllers;

import Services.CurrentSession;
import Services.DatabaseManager;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("[MainController] initialize - initDatabase");
        try {
            DatabaseManager.initDatabase();
        } catch (SQLException e) {
            System.err.println("[MainController] initDatabase error: " + e.getMessage());
            e.printStackTrace();
        }
        // dodatkowo można dostosować widoki/taby: np. jeśli PODATNIK, wyłączyć zakładkę 'Pracownicy'
        if (CurrentSession.isPodatnik()) {
            // tu trzeba pobrać referencję TabPane i wyłączyć odpowiednie Taby;
            // można w FXML nadać fx:id dla TabPane i potem w kodzie:
            // tabPane.getTabs().remove(pracownicyTab); itd.
        }
    }
}
