package Controllers;

import Model.Podatnik;
import Model.Wniosek;
import Services.CurrentSession;
import Services.WniosekService;
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

public class WniosekController {
    @FXML private ComboBox<Podatnik> podatnikCombo;
    @FXML private TextArea trescField;
    @FXML private TextField searchField;
    @FXML private TableView<Wniosek> table;
    @FXML private TableColumn<Wniosek, String> idColumn;
    @FXML private TableColumn<Wniosek, String> podatnikNameColumn;
    @FXML private TableColumn<Wniosek, String> trescColumn;
    @FXML private TableColumn<Wniosek, String> statusColumn;
    @FXML private TableColumn<Wniosek, String> dataColumn;
    @FXML private TableColumn<Wniosek, Void> actionColumn;
    @FXML private Button btnZloz;

    private final PodatnikDAO podatnikDAO = new PodatnikDAO();
    private final WniosekService service = new WniosekService();
    private final ObservableList<Wniosek> masterData = FXCollections.observableArrayList();
    private final FilteredList<Wniosek> filteredData = new FilteredList<>(masterData, p->true);
    private final ObservableList<Podatnik> podatnicy = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        podatnikNameColumn.setCellValueFactory(cell -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                        podatnikDAO.findById(cell.getValue().getPodatnikId())
                                .map(p->p.getImie()+" "+p.getNazwisko()).orElse(cell.getValue().getPodatnikId()));
            } catch (SQLException e) {
                e.printStackTrace();
                return new javafx.beans.property.SimpleStringProperty(cell.getValue().getPodatnikId());
            }
        });
        trescColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTresc()));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus().name()));
        dataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataZlozenia().toString()));
        table.setItems(filteredData);

        loadPodatnicy();
        loadData();
        addActionButtons();
        setupSearch();

        // tylko PODATNIK może składać wnioski
        if (CurrentSession.isPodatnik()) {
            btnZloz.setDisable(false);
        } else {
            btnZloz.setDisable(true);
        }
        // w combo podatnik: jeśli PODATNIK to tylko on sam, jeśli PRACOWNIK: lista wszystkich
    }

    private void loadPodatnicy() {
        podatnicy.clear();
        try {
            if (CurrentSession.isPodatnik()) {
                podatnikDAO.findById(CurrentSession.getUserId()).ifPresent(podatnicy::add);
            } else {
                podatnicy.addAll(podatnikDAO.findAll());
            }
            podatnikCombo.setItems(podatnicy);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać podatników:\n" + e.getMessage());
        }
    }

    private void loadData() {
        masterData.clear();
        try {
            List<Wniosek> list = service.wszystkieWnioski();
            if (CurrentSession.isPodatnik()) {
                list.removeIf(w -> !w.getPodatnikId().equals(CurrentSession.getUserId()));
            }
            masterData.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać wniosków:\n" + e.getMessage());
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
                        if (p != null) {
                            String full = (p.getImie()+" "+p.getNazwisko()).toLowerCase();
                            if (!full.contains(fl)) return false;
                        } else return false;
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
    private void onZloz() {
        Podatnik p = podatnikCombo.getValue();
        String tresc = trescField.getText().trim();
        if (p == null || tresc.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Brak danych", "Wybierz podatnika i wprowadź treść wniosku.");
            return;
        }
        try {
            Wniosek w = service.zlozWniosek(p.getId(), tresc);
            masterData.add(w);
            trescField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się złożyć wniosku:\n" + e.getMessage());
        }
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Wniosek, Void> call(TableColumn<Wniosek, Void> param) {
                return new TableCell<>() {
                    private final ChoiceBox<String> choice = new ChoiceBox<>(FXCollections.observableArrayList("ZLOZONY", "W_TRAKCIE", "ROZPATRZONY"));
                    private final Button btn = new Button("Zmień");
                    {
                        btn.setOnAction(event -> {
                            Wniosek w = getTableView().getItems().get(getIndex());
                            String val = choice.getValue();
                            if (val != null) {
                                // tylko PRACOWNIK może zmieniać status
                                if (!CurrentSession.isPracownik()) {
                                    showAlert(Alert.AlertType.WARNING, "Brak uprawnień", "Tylko pracownik może zmieniać status wniosku.");
                                    return;
                                }
                                try {
                                    Wniosek.Status status = Wniosek.Status.valueOf(val);
                                    service.zmienStatus(w.getId(), status);
                                    w.setStatus(status);
                                    table.refresh();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zmienić statusu:\n" + e.getMessage());
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
                            Wniosek w = getTableView().getItems().get(getIndex());
                            choice.setValue(w.getStatus().name());
                            // jeśli PRACOWNIK, aktywne, inaczej wyłączone
                            choice.setDisable(!CurrentSession.isPracownik());
                            btn.setDisable(!CurrentSession.isPracownik());
                            setGraphic(new HBox(5, choice, btn));
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
