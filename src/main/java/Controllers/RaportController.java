package Controllers;

import Model.Mandat;
import Model.Wizyta;
import Model.Wniosek;
import Services.MandatService;
import Services.WizytaService;
import Services.WniosekService;
import Services.RaportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class RaportController {
    @FXML private CheckBox onlyPaidCheck;
    @FXML private TableView<Mandat> mandatTable;
    @FXML private TableColumn<Mandat, String> midColumn;
    @FXML private TableColumn<Mandat, String> mpodatnikIdColumn;
    @FXML private TableColumn<Mandat, String> mkwotaColumn;
    @FXML private TableColumn<Mandat, String> moplaconyColumn;
    @FXML private TableColumn<Mandat, String> mdataColumn;

    @FXML private TableView<Wizyta> wizytaTable;
    @FXML private TableColumn<Wizyta, String> widColumn;
    @FXML private TableColumn<Wizyta, String> wpodatnikIdColumn;
    @FXML private TableColumn<Wizyta, String> wpracownikIdColumn;
    @FXML private TableColumn<Wizyta, String> wdataColumn;

    @FXML private ChoiceBox<String> statusChoice;
    @FXML private TableView<Wniosek> wniosekTable;
    @FXML private TableColumn<Wniosek, String> zidColumn;
    @FXML private TableColumn<Wniosek, String> zpodatnikIdColumn;
    @FXML private TableColumn<Wniosek, String> ztrescColumn;
    @FXML private TableColumn<Wniosek, String> zstatusColumn;
    @FXML private TableColumn<Wniosek, String> zdataColumn;

    private final MandatService ms = new MandatService();
    private final WizytaService vs = new WizytaService();
    private final WniosekService ws = new WniosekService();
    private final RaportService rs = new RaportService(ms, ws, vs);

    private final ObservableList<Mandat> mandatData = FXCollections.observableArrayList();
    private final ObservableList<Wizyta> wizytaData = FXCollections.observableArrayList();
    private final ObservableList<Wniosek> wniosekData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Mandat columns
        midColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        mpodatnikIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPodatnikId()));
        mkwotaColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getKwota())));
        moplaconyColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().isOplacony() ? "Tak" : "Nie"));
        mdataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataWystawienia().toString()));
        mandatTable.setItems(mandatData);

        // Wizyta columns
        widColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        wpodatnikIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPodatnikId()));
        wpracownikIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPracownikId()));
        wdataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getData().toString()));
        wizytaTable.setItems(wizytaData);

        // Wniosek columns
        zidColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        zpodatnikIdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPodatnikId()));
        ztrescColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTresc()));
        zstatusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus().name()));
        zdataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataZlozenia().toString()));
        wniosekTable.setItems(wniosekData);

        statusChoice.setItems(FXCollections.observableArrayList("ZLOZONY", "W_TRAKCIE", "ROZPATRZONY"));
        statusChoice.setValue("ZLOZONY");

        loadMandaty();
        loadWizyty();
        loadWnioski();
    }

    @FXML
    private void onFilterMandaty() {
        loadMandaty();
    }
    private void loadMandaty() {
        mandatData.clear();
        try {
            List<Mandat> list = rs.raportMandatow(onlyPaidCheck.isSelected());
            mandatData.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać raportu mandatów:\n" + e.getMessage());
        }
    }

    private void loadWizyty() {
        wizytaData.clear();
        try {
            List<Wizyta> list = rs.raportWizyt();
            wizytaData.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać raportu wizyt:\n" + e.getMessage());
        }
    }

    @FXML
    private void onFilterWnioski() {
        loadWnioski();
    }
    private void loadWnioski() {
        wniosekData.clear();
        try {
            Wniosek.Status st = Wniosek.Status.valueOf(statusChoice.getValue());
            List<Wniosek> list = rs.raportWnioskow(st);
            wniosekData.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie udało się pobrać raportu wniosków:\n" + e.getMessage());
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
