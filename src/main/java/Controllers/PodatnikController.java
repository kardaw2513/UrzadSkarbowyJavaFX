package Controllers;

import Model.Podatnik;
import Services.CurrentSession;
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
    @FXML private Button btnAdd;
    @FXML private TableView<Podatnik> table;
    @FXML private TableColumn<Podatnik, String> idColumn;
    @FXML private TableColumn<Podatnik, String> imieColumn;
    @FXML private TableColumn<Podatnik, String> nazwiskoColumn;
    @FXML private TextField searchField;

    private final PodatnikDAO dao = new PodatnikDAO();
    private final ObservableList<Podatnik> masterData = FXCollections.observableArrayList();
    private final javafx.collections.transformation.FilteredList<Podatnik> filteredData = new javafx.collections.transformation.FilteredList<>(masterData, p->true);

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        imieColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getImie()));
        nazwiskoColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNazwisko()));
        table.setItems(filteredData);

        // tylko pracownik może dodawać nowych podatników
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
                dao.findById(CurrentSession.getUserId()).ifPresent(masterData::add);
            } else {
                masterData.addAll(dao.findAll());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać podatników:\n" + e.getMessage());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, o, n) -> {
            String fl = n.trim().toLowerCase();
            if (fl.isEmpty()) {
                filteredData.setPredicate(p -> {
                    if (CurrentSession.isPodatnik()) {
                        return p.getId().equals(CurrentSession.getUserId());
                    }
                    return true;
                });
            } else {
                filteredData.setPredicate(p -> {
                    String full = (p.getImie()+" "+p.getNazwisko()).toLowerCase();
                    boolean ok = full.contains(fl);
                    if (!ok) return false;
                    if (CurrentSession.isPodatnik()) {
                        return p.getId().equals(CurrentSession.getUserId());
                    }
                    return true;
                });
            }
        });
    }

    @FXML
    private void onAddPodatnik() {
        String imie = imieField.getText().trim();
        String nazw = nazwiskoField.getText().trim();
        if (imie.isEmpty() || nazw.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Podaj imię i nazwisko.");
            return;
        }
        Podatnik p = new Podatnik(imie, nazw);
        try {
            dao.save(p);
            masterData.add(p);
            imieField.clear();
            nazwiskoField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się dodać podatnika:\n" + e.getMessage());
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
