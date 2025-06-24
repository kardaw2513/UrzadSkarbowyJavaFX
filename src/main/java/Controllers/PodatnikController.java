package Controllers;

import Model.Podatnik;
import Services.dao.PodatnikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class PodatnikController {
    @FXML private TextField imieField;
    @FXML private TextField nazwiskoField;
    @FXML private TableView<Podatnik> table;
    @FXML private TableColumn<Podatnik, String> idColumn;
    @FXML private TableColumn<Podatnik, String> imieColumn;
    @FXML private TableColumn<Podatnik, String> nazwiskoColumn;

    private final PodatnikDAO dao = new PodatnikDAO();
    private final ObservableList<Podatnik> data = FXCollections.observableArrayList();

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
            List<Podatnik> list = dao.findAll();
            data.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać listy podatników:\n" + e.getMessage());
        }
    }

    @FXML
    private void onAddPodatnik() {
        String imie = imieField.getText().trim();
        String nazwisko = nazwiskoField.getText().trim();
        if (imie.isEmpty() || nazwisko.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Podaj imię i nazwisko.");
            return;
        }
        Podatnik p = new Podatnik(imie, nazwisko);
        try {
            dao.save(p);
            data.add(p);
            imieField.clear();
            nazwiskoField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie udało się dodać podatnika:\n" + e.getMessage());
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
