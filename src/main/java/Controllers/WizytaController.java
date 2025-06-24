package Controllers;

import Model.Podatnik;
import Model.Pracownik;
import Model.Wizyta;
import Services.WizytaService;
import Services.dao.PodatnikDAO;
import Services.dao.PracownikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class WizytaController {
    @FXML private ComboBox<Podatnik> podatnikCombo;
    @FXML private ComboBox<Pracownik> pracownikCombo;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Wizyta> table;
    @FXML private TableColumn<Wizyta, String> idColumn;
    @FXML private TableColumn<Wizyta, String> podatnikIdColumn;
    @FXML private TableColumn<Wizyta, String> pracownikIdColumn;
    @FXML private TableColumn<Wizyta, String> dataColumn;

    private final PodatnikDAO podatnikDAO = new PodatnikDAO();
    private final PracownikDAO pracownikDAO = new PracownikDAO();
    private final WizytaService service = new WizytaService();
    private final ObservableList<Wizyta> data = FXCollections.observableArrayList();
    private final ObservableList<Podatnik> podatnicy = FXCollections.observableArrayList();
    private final ObservableList<Pracownik> pracownicy = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));
        podatnikIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPodatnikId()));
        pracownikIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPracownikId()));
        dataColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getData().toString()));
        table.setItems(data);
        loadCombos();
        loadData();
    }

    private void loadCombos() {
        podatnicy.clear();
        pracownicy.clear();
        try {
            List<Podatnik> listP = podatnikDAO.findAll();
            List<Pracownik> listPr = pracownikDAO.findAll();
            podatnicy.addAll(listP);
            pracownicy.addAll(listPr);
            podatnikCombo.setItems(podatnicy);
            pracownikCombo.setItems(pracownicy);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się wczytać listy podatników/pracowników:\n" + e.getMessage());
        }
    }

    private void loadData() {
        data.clear();
        try {
            List<Wizyta> list = service.wszystkieWizyty();
            data.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się wczytać wizyt:\n" + e.getMessage());
        }
    }

    @FXML
    private void onUmow() {
        Podatnik p = podatnikCombo.getValue();
        Pracownik pr = pracownikCombo.getValue();
        LocalDate d = datePicker.getValue();
        if (p == null || pr == null || d == null) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Wybierz podatnika, pracownika i datę wizyty.");
            return;
        }
        if (d.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Niepoprawna data", "Data wizyty nie może być z przeszłości.");
            return;
        }
        try {
            Wizyta w = service.umowWizyte(p.getId(), d, pr.getId());
            data.add(w);
            datePicker.setValue(null);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie udało się umówić wizyty:\n" + e.getMessage());
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
