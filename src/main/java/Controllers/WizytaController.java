package Controllers;

import Model.Podatnik;
import Model.Pracownik;
import Model.Wizyta;
import Services.CurrentSession;
import Services.WizytaService;
import Services.dao.PodatnikDAO;
import Services.dao.PracownikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class WizytaController {
    @FXML private ComboBox<Podatnik> podatnikCombo;
    @FXML private ComboBox<Pracownik> pracownikCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField searchField;
    @FXML private TableView<Wizyta> table;
    @FXML private TableColumn<Wizyta, String> idColumn;
    @FXML private TableColumn<Wizyta, String> podatnikNameColumn;
    @FXML private TableColumn<Wizyta, String> pracownikNameColumn;
    @FXML private TableColumn<Wizyta, String> dataColumn;
    @FXML private Button btnUmow;

    private final PodatnikDAO podatnikDAO = new PodatnikDAO();
    private final PracownikDAO pracownikDAO = new PracownikDAO();
    private final WizytaService service = new WizytaService();
    private final ObservableList<Wizyta> masterData = FXCollections.observableArrayList();
    private final FilteredList<Wizyta> filteredData = new FilteredList<>(masterData, p->true);
    private final ObservableList<Podatnik> podatnicy = FXCollections.observableArrayList();
    private final ObservableList<Pracownik> pracownicy = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));
        podatnikNameColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(
                        podatnikDAO.findById(cell.getValue().getPodatnikId())
                                .map(p->p.getImie()+" "+p.getNazwisko()).orElse(cell.getValue().getPodatnikId()));
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty(cell.getValue().getPodatnikId());
            }
        });
        pracownikNameColumn.setCellValueFactory(cell -> {
            try {
                return new SimpleStringProperty(
                        pracownikDAO.findById(cell.getValue().getPracownikId())
                                .map(p->p.getImie()+" "+p.getNazwisko()).orElse(cell.getValue().getPracownikId()));
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty(cell.getValue().getPracownikId());
            }
        });
        dataColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getData().toString()));
        table.setItems(filteredData);

        setupCombos();
        loadData();
        setupSearch();

        // jeżeli rola PODATNIK: użytkownik może umawiać wizyty tylko dla siebie, ale widzi tylko swoje wizyty
        if (CurrentSession.isPodatnik()) {
            btnUmow.setDisable(false);
        }
        if (CurrentSession.isPodatnik()) {
            // podatnikCombo ograniczony do siebie, pracownikCombo do wszystkich (mogą wybrać dowolnego pracownika)
            // tabela tylko jego wizyty
        }
    }

    private void setupCombos() {
        podatnicy.clear();
        pracownicy.clear();
        try {
            if (CurrentSession.isPodatnik()) {
                podatnikDAO.findById(CurrentSession.getUserId()).ifPresent(podatnicy::add);
            } else {
                podatnicy.addAll(podatnikDAO.findAll());
            }
            pracownicy.addAll(pracownikDAO.findAll());
            podatnikCombo.setItems(podatnicy);
            pracownikCombo.setItems(pracownicy);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się wczytać list:\n" + e.getMessage());
        }
    }

    private void loadData() {
        masterData.clear();
        try {
            List<Wizyta> list = service.wszystkieWizyty();
            if (CurrentSession.isPodatnik()) {
                list.removeIf(w -> !w.getPodatnikId().equals(CurrentSession.getUserId()));
            }
            masterData.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się wczytać wizyt:\n" + e.getMessage());
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String fl = newV.trim().toLowerCase();
            if (fl.isEmpty()) {
                filteredData.setPredicate(w -> {
                    if (CurrentSession.isPodatnik()) {
                        return w.getPodatnikId().equals(CurrentSession.getUserId());
                    }
                    return true;
                });
            } else {
                filteredData.setPredicate(w -> {
                    try {
                        Podatnik p = podatnikDAO.findById(w.getPodatnikId()).orElse(null);
                        Pracownik pr = pracownikDAO.findById(w.getPracownikId()).orElse(null);
                        boolean matchP = p != null && (p.getImie()+" "+p.getNazwisko()).toLowerCase().contains(fl);
                        boolean matchPr = pr != null && (pr.getImie()+" "+pr.getNazwisko()).toLowerCase().contains(fl);
                        if (!(matchP || matchPr)) return false;
                        if (CurrentSession.isPodatnik()) {
                            return w.getPodatnikId().equals(CurrentSession.getUserId());
                        }
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                });
            }
        });
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
            masterData.add(w);
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
