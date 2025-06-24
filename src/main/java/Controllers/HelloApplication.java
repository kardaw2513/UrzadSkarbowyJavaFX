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
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        // inicjalizacja bazy
        System.out.println("[HelloApplication] Start aplikacji, initDatabase...");
        try {
            DatabaseManager.initDatabase();
        } catch (SQLException e) {
            System.err.println("[HelloApplication] Błąd initDatabase: " + e.getMessage());
            e.printStackTrace();
        }
        // debug listowania tabel
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
        showLoginWindow();
    }

    private void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            Stage loginStage = new Stage();
            loginStage.setTitle("Logowanie");
            loginStage.setScene(scene);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/fxml/main.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(HelloApplication.class.getResource("/css/application.css").toExternalForm());
            primaryStage.setTitle("Urzad Skarbowy");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
