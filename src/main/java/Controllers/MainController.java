package Controllers;

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
    }
}
