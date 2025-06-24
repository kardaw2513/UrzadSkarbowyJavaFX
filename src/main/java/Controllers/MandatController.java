package Controllers;

import Model.Mandat;
import Model.Podatnik;
import Services.CurrentSession;
import Services.MandatService;
import Services.dao.PodatnikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.List;

public class MandatController {
    @FXML private ComboBox<Podatnik> podatnikCombo;
    @FXML private TextField kwotaField;
    @FXML private TextField searchField;
    @FXML private TableView<Mandat> table;
    @FXML private TableColumn<Mandat, String> idColumn;
    @FXML private TableColumn<Mandat, String> podatnikNameColumn;
    @FXML private TableColumn<Mandat, String> kwotaColumn;
    @FXML private TableColumn<Mandat, String> oplaconyColumn;
    @FXML private TableColumn<Mandat, String> dataColumn;
    @FXML private TableColumn<Mandat, Void> actionColumn;
    @FXML private Button btnWystaw;

    private final PodatnikDAO podatnikDAO = new PodatnikDAO();
    private final MandatService service = new MandatService();
    private final ObservableList<Mandat> masterData = FXCollections.observableArrayList();
    private final FilteredList<Mandat> filteredData = new FilteredList<>(masterData, p -> true);
    private final ObservableList<Podatnik> podatnicy = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // kolumny
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        podatnikNameColumn.setCellValueFactory(cell -> {
            String pid = cell.getValue().getPodatnikId();
            try {
                return new javafx.beans.property.SimpleStringProperty(
                        podatnikDAO.findById(pid)
                                .map(p->p.getImie()+" "+p.getNazwisko()).orElse(pid));
            } catch (SQLException e) {
                e.printStackTrace();
                return new javafx.beans.property.SimpleStringProperty(pid);
            }
        });
        kwotaColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getKwota())));
        oplaconyColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().isOplacony() ? "Tak" : "Nie"));
        dataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataWystawienia().toString()));
        table.setItems(filteredData);

        loadPodatnicy();
        loadData();
        addActionButtons();
        setupSearch();

        // ukryj przycisk wystawiania dla roli PODATNIK
        if (CurrentSession.isPodatnik()) {
            btnWystaw.setDisable(true);
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String filter = newV.trim().toLowerCase();
            if (filter.isEmpty()) {
                filteredData.setPredicate(p -> {
                    if (CurrentSession.isPodatnik()) {
                        return p.getPodatnikId().equals(CurrentSession.getUserId());
                    }
                    return true;
                });
            } else {
                filteredData.setPredicate(m -> {
                    // pobierz podatnika
                    try {
                        Podatnik p = podatnikDAO.findById(m.getPodatnikId()).orElse(null);
                        if (p != null) {
                            String full = (p.getImie()+" "+p.getNazwisko()).toLowerCase();
                            boolean matches = full.contains(filter);
                            if (!matches) return false;
                        } else return false;
                        if (CurrentSession.isPodatnik()) {
                            return m.getPodatnikId().equals(CurrentSession.getUserId());
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

    private void loadPodatnicy() {
        podatnicy.clear();
        try {
            if (CurrentSession.isPodatnik()) {
                // tylko siebie
                podatnikDAO.findById(CurrentSession.getUserId()).ifPresent(podatnicy::add);
            } else {
                podatnicy.addAll(podatnikDAO.findAll());
            }
            podatnikCombo.setItems(podatnicy);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać podatników:\n" + e.getMessage());
        }
    }

    private void loadData() {
        masterData.clear();
        try {
            List<Mandat> list = service.wszystkieMandaty();
            if (CurrentSession.isPodatnik()) {
                list.removeIf(m -> !m.getPodatnikId().equals(CurrentSession.getUserId()));
            }
            masterData.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać mandatów:\n" + e.getMessage());
        }
    }

    @FXML
    private void onWystawMandat() {
        Podatnik p = podatnikCombo.getValue();
        String kw = kwotaField.getText().trim();
        if (p == null || kw.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Wybierz podatnika i podaj kwotę.");
            return;
        }
        double kwota;
        try {
            kwota = Double.parseDouble(kw);
            if (kwota <= 0) {
                showAlert(Alert.AlertType.WARNING, "Niepoprawna kwota", "Kwota musi być dodatnia.");
                return;
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, "Niepoprawna kwota", "Podaj poprawną liczbę.");
            return;
        }
        try {
            Mandat m = service.wystawMandat(p.getId(), kwota);
            masterData.add(m);
            kwotaField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie udało się wystawić mandatu:\n" + e.getMessage());
        }
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Mandat, Void> call(TableColumn<Mandat, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Opłać");
                    {
                        btn.setOnAction(event -> {
                            Mandat m = getTableView().getItems().get(getIndex());
                            if (!m.isOplacony()) {
                                try {
                                    service.zaplacMandat(m.getId());
                                    m.zaplac();
                                    table.refresh();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie udało się opłacić mandatu:\n" + e.getMessage());
                                }
                            }
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Mandat m = getTableView().getItems().get(getIndex());
                            btn.setDisable(m.isOplacony() || (CurrentSession.isPodatnik() && !m.getPodatnikId().equals(CurrentSession.getUserId())));
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
