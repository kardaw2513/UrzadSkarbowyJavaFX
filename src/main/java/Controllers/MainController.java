// src/main/java/Controllers/MainController.java
package Controllers;

import Services.DatabaseManager;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DatabaseManager.initDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            // Można tu dodać alert informujący o błędzie inicjalizacji DB
        }
    }
}
