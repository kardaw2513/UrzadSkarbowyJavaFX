package Controllers;

import Model.Pracownik;
import Services.CurrentSession;
import Services.dao.PracownikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class PracownikController {
    @FXML private TextField imieField;
    @FXML private TextField nazwiskoField;
    @FXML private Button btnAdd;
    @FXML private TableView<Pracownik> table;
    @FXML private TableColumn<Pracownik, String> idColumn;
    @FXML private TableColumn<Pracownik, String> imieColumn;
    @FXML private TableColumn<Pracownik, String> nazwiskoColumn;
    @FXML private TextField searchField;

    private final PracownikDAO dao = new PracownikDAO();
    private final ObservableList<Pracownik> masterData = FXCollections.observableArrayList();
    private final FilteredList<Pracownik> filteredData = new FilteredList<>(masterData, p->true);

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        imieColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getImie()));
        nazwiskoColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNazwisko()));
        table.setItems(filteredData);

        // tylko pracownik może dodawać pracowników; podatnik nie
        if (CurrentSession.isPodatnik()) {
            btnAdd.setDisable(true);
            imieField.setDisable(true);
            nazwiskoField.setDisable(true);
        }
        loadData();
        setupSearch();
    }

    private void loadData() {
        masterData.clear();
        try {
            if (CurrentSession.isPodatnik()) {
                // podatnik nie widzi listy pracowników; można ukryć tabelę albo pokazać pustą
                // tu: pokazujemy pustą listę
            } else {
                masterData.addAll(dao.findAll());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać pracowników:\n" + e.getMessage());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, o, n) -> {
            String fl = n.trim().toLowerCase();
            if (fl.isEmpty()) {
                filteredData.setPredicate(p -> true);
            } else {
                filteredData.setPredicate(p -> {
                    String full = (p.getImie()+" "+p.getNazwisko()).toLowerCase();
                    return full.contains(fl);
                });
            }
        });
    }

    @FXML
    private void onAddPracownik() {
        String imie = imieField.getText().trim();
        String nazw = nazwiskoField.getText().trim();
        if (imie.isEmpty() || nazw.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Podaj imię i nazwisko.");
            return;
        }
        Pracownik p = new Pracownik(imie, nazw);
        try {
            dao.save(p);
            masterData.add(p);
            imieField.clear();
            nazwiskoField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się dodać pracownika:\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
