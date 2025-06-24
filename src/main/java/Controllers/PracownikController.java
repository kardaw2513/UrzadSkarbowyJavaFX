package Controllers;

import Model.Pracownik;
import Services.dao.PracownikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class PracownikController {
    @FXML private TextField imieField;
    @FXML private TextField nazwiskoField;
    @FXML private TableView<Pracownik> table;
    @FXML private TableColumn<Pracownik, String> idColumn;
    @FXML private TableColumn<Pracownik, String> imieColumn;
    @FXML private TableColumn<Pracownik, String> nazwiskoColumn;

    private final PracownikDAO dao = new PracownikDAO();
    private final ObservableList<Pracownik> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        imieColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getImie()));
        nazwiskoColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNazwisko()));
        table.setItems(data);
        loadData();
    }

    private void loadData() {
        data.clear();
        try {
            List<Pracownik> list = dao.findAll();
            data.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać listy pracowników:\n" + e.getMessage());
        }
    }

    @FXML
    private void onAddPracownik() {
        String imie = imieField.getText().trim();
        String nazwisko = nazwiskoField.getText().trim();
        if (imie.isEmpty() || nazwisko.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Podaj imię i nazwisko.");
            return;
        }
        Pracownik p = new Pracownik(imie, nazwisko);
        try {
            dao.save(p);
            data.add(p);
            imieField.clear();
            nazwiskoField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie udało się dodać pracownika:\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
