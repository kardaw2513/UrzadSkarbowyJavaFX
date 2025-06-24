package Controllers;

import Services.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("[HelloApplication] Start aplikacji, initDatabase...");
        try {
            DatabaseManager.initDatabase();
        } catch (SQLException e) {
            System.err.println("[HelloApplication] Błąd initDatabase: " + e.getMessage());
            e.printStackTrace();
        }
        // Debug: listowanie tabel w bazie
        try {
            System.out.println("[HelloApplication] Sprawdzam, jakie tabele istnieją:");
            Statement stmt = DatabaseManager.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            while (rs.next()) {
                System.out.println("  tabela: " + rs.getString("name"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("[HelloApplication] Błąd przy listowaniu tabel: " + e.getMessage());
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Urzad Skarbowy");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
