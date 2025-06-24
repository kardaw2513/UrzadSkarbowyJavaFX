package Controllers;

import Model.Podatnik;
import Model.Wniosek;
import Services.WniosekService;
import Services.dao.PodatnikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.List;

public class WniosekController {
    @FXML private ComboBox<Podatnik> podatnikCombo;
    @FXML private TextField trescField;
    @FXML private TableView<Wniosek> table;
    @FXML private TableColumn<Wniosek, String> idColumn;
    @FXML private TableColumn<Wniosek, String> podatnikIdColumn;
    @FXML private TableColumn<Wniosek, String> trescColumn;
    @FXML private TableColumn<Wniosek, String> statusColumn;
    @FXML private TableColumn<Wniosek, String> dataColumn;
    @FXML private TableColumn<Wniosek, Void> actionColumn;

    private final PodatnikDAO podatnikDAO = new PodatnikDAO();
    private final WniosekService service = new WniosekService();
    private final ObservableList<Wniosek> data = FXCollections.observableArrayList();
    private final ObservableList<Podatnik> podatnicy = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        podatnikIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPodatnikId()));
        trescColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTresc()));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus().name()));
        dataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataZlozenia().toString()));
        table.setItems(data);

        loadPodatnicy();
        loadData();
        addActionButtons();
    }

    private void loadPodatnicy() {
        podatnicy.clear();
        try {
            podatnicy.addAll(podatnikDAO.findAll());
            podatnikCombo.setItems(podatnicy);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać podatników:\n" + e.getMessage());
        }
    }

    private void loadData() {
        data.clear();
        try {
            data.addAll(service.wszystkieWnioski());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać wniosków:\n" + e.getMessage());
        }
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
            data.add(w);
            trescField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie udało się złożyć wniosku:\n" + e.getMessage());
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
                                try {
                                    Wniosek.Status status = Wniosek.Status.valueOf(val);
                                    service.zmienStatus(w.getId(), status);
                                    w.setStatus(status);
                                    table.refresh();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie udało się zmienić statusu:\n" + e.getMessage());
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
