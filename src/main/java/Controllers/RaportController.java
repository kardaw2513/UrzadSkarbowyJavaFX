package Controllers;

import Model.Mandat;
import Model.Podatnik;
import Model.Pracownik;
import Model.Wizyta;
import Model.Wniosek;
import Services.CurrentSession;
import Services.MandatService;
import Services.RaportService;
import Services.WizytaService;
import Services.WniosekService;
import Services.dao.PodatnikDAO;
import Services.dao.PracownikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class RaportController {
    @FXML private CheckBox onlyPaidCheck;
    @FXML private TextField searchMandatField;
    @FXML private TableView<Mandat> mandatTable;
    @FXML private TableColumn<Mandat, String> midColumn;
    @FXML private TableColumn<Mandat, String> mpodatnikNameColumn;
    @FXML private TableColumn<Mandat, String> mkwotaColumn;
    @FXML private TableColumn<Mandat, String> moplaconyColumn;
    @FXML private TableColumn<Mandat, String> mdataColumn;

    @FXML private TextField searchWizytaField;
    @FXML private TableView<Wizyta> wizytaTable;
    @FXML private TableColumn<Wizyta, String> widColumn;
    @FXML private TableColumn<Wizyta, String> wpodatnikNameColumn;
    @FXML private TableColumn<Wizyta, String> wpracownikNameColumn;
    @FXML private TableColumn<Wizyta, String> wdataColumn;

    @FXML private ChoiceBox<String> statusChoice;
    @FXML private TextField searchWniosekField;
    @FXML private TableView<Wniosek> wniosekTable;
    @FXML private TableColumn<Wniosek, String> zidColumn;
    @FXML private TableColumn<Wniosek, String> zpodatnikNameColumn;
    @FXML private TableColumn<Wniosek, String> ztrescColumn;
    @FXML private TableColumn<Wniosek, String> zstatusColumn;
    @FXML private TableColumn<Wniosek, String> zdataColumn;
    @FXML private TableColumn<Wniosek, Void> zactionColumn;

    private final MandatService ms = new MandatService();
    private final WizytaService vs = new WizytaService();
    private final WniosekService ws = new WniosekService();
    private final RaportService rs = new RaportService(ms, ws, vs);
    private final PodatnikDAO podatnikDAO = new PodatnikDAO();
    private final PracownikDAO pracownikDAO = new PracownikDAO();

    private final ObservableList<Mandat> mandatMaster = FXCollections.observableArrayList();
    private final FilteredList<Mandat> mandatFiltered = new FilteredList<>(mandatMaster, p->true);

    private final ObservableList<Wizyta> wizytaMaster = FXCollections.observableArrayList();
    private final FilteredList<Wizyta> wizytaFiltered = new FilteredList<>(wizytaMaster, p->true);

    private final ObservableList<Wniosek> wniosekMaster = FXCollections.observableArrayList();
    private final FilteredList<Wniosek> wniosekFiltered = new FilteredList<>(wniosekMaster, p->true);

    @FXML
    public void initialize() {
        // Mandaty kolumny
        midColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        mpodatnikNameColumn.setCellValueFactory(cell -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                        podatnikDAO.findById(cell.getValue().getPodatnikId())
                                .map(p->p.getImie()+" "+p.getNazwisko()).orElse(cell.getValue().getPodatnikId()));
            } catch (SQLException e) {
                e.printStackTrace();
                return new javafx.beans.property.SimpleStringProperty(cell.getValue().getPodatnikId());
            }
        });
        mkwotaColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getKwota())));
        moplaconyColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().isOplacony() ? "Tak" : "Nie"));
        mdataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataWystawienia().toString()));
        mandatTable.setItems(mandatFiltered);

        // Wizyty
        widColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        wpodatnikNameColumn.setCellValueFactory(cell -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                        podatnikDAO.findById(cell.getValue().getPodatnikId())
                                .map(p->p.getImie()+" "+p.getNazwisko()).orElse(cell.getValue().getPodatnikId()));
            } catch (SQLException e) {
                e.printStackTrace();
                return new javafx.beans.property.SimpleStringProperty(cell.getValue().getPodatnikId());
            }
        });
        wpracownikNameColumn.setCellValueFactory(cell -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                        pracownikDAO.findById(cell.getValue().getPracownikId())
                                .map(p->p.getImie()+" "+p.getNazwisko()).orElse(cell.getValue().getPracownikId()));
            } catch (SQLException e) {
                e.printStackTrace();
                return new javafx.beans.property.SimpleStringProperty(cell.getValue().getPracownikId());
            }
        });
        wdataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getData().toString()));
        wizytaTable.setItems(wizytaFiltered);

        // Wnioski
        zidColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getId()));
        zpodatnikNameColumn.setCellValueFactory(cell -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(
                        podatnikDAO.findById(cell.getValue().getPodatnikId())
                                .map(p->p.getImie()+" "+p.getNazwisko()).orElse(cell.getValue().getPodatnikId()));
            } catch (SQLException e) {
                e.printStackTrace();
                return new javafx.beans.property.SimpleStringProperty(cell.getValue().getPodatnikId());
            }
        });
        ztrescColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTresc()));
        zstatusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatus().name()));
        zdataColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDataZlozenia().toString()));
        wniosekTable.setItems(wniosekFiltered);

        statusChoice.setItems(FXCollections.observableArrayList("ZLOZONY", "W_TRAKCIE", "ROZPATRZONY"));
        statusChoice.setValue("ZLOZONY");

        loadMandaty();
        loadWizyty();
        loadWnioski();
        setupSearchMandat();
        setupSearchWizyta();
        setupSearchWniosek();
    }

    @FXML
    private void onFilterMandaty() {
        loadMandaty();
        applySearchMandat();
    }
    private void loadMandaty() {
        mandatMaster.clear();
        try {
            List<Mandat> list = rs.raportMandatow(onlyPaidCheck.isSelected());
            if (CurrentSession.isPodatnik()) {
                list.removeIf(m -> !m.getPodatnikId().equals(CurrentSession.getUserId()));
            }
            mandatMaster.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać mandatów:\n" + e.getMessage());
        }
    }
    private void setupSearchMandat() {
        searchMandatField.textProperty().addListener((obs, o, n) -> applySearchMandat());
    }
    private void applySearchMandat() {
        String fl = searchMandatField.getText().trim().toLowerCase();
        if (fl.isEmpty()) {
            mandatFiltered.setPredicate(m -> {
                if (CurrentSession.isPodatnik()) {
                    return m.getPodatnikId().equals(CurrentSession.getUserId());
                }
                return true;
            });
        } else {
            mandatFiltered.setPredicate(m -> {
                try {
                    Podatnik p = podatnikDAO.findById(m.getPodatnikId()).orElse(null);
                    if (p != null) {
                        String full = (p.getImie()+" "+p.getNazwisko()).toLowerCase();
                        if (!full.contains(fl)) return false;
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
    }

    @FXML
    private void onFilterWnioski() {
        loadWnioski();
        applySearchWniosek();
    }
    private void loadWnioski() {
        wniosekMaster.clear();
        try {
            Wniosek.Status st = Wniosek.Status.valueOf(statusChoice.getValue());
            List<Wniosek> list = rs.raportWnioskow(st);
            if (CurrentSession.isPodatnik()) {
                list.removeIf(w -> !w.getPodatnikId().equals(CurrentSession.getUserId()));
            }
            wniosekMaster.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać wniosków:\n" + e.getMessage());
        }
    }
    private void setupSearchWniosek() {
        searchWniosekField.textProperty().addListener((obs, o, n) -> applySearchWniosek());
    }
    private void applySearchWniosek() {
        String fl = searchWniosekField.getText().trim().toLowerCase();
        if (fl.isEmpty()) {
            wniosekFiltered.setPredicate(w -> {
                if (CurrentSession.isPodatnik()) {
                    return w.getPodatnikId().equals(CurrentSession.getUserId());
                }
                return true;
            });
        } else {
            wniosekFiltered.setPredicate(w -> {
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
    }

    private void setupSearchWizyta() {
        searchWizytaField.textProperty().addListener((obs, o, n) -> applySearchWizyta());
    }
    private void applySearchWizyta() {
        String fl = searchWizytaField.getText().trim().toLowerCase();
        if (fl.isEmpty()) {
            wizytaFiltered.setPredicate(w -> {
                if (CurrentSession.isPodatnik()) {
                    return w.getPodatnikId().equals(CurrentSession.getUserId());
                }
                return true;
            });
        } else {
            wizytaFiltered.setPredicate(w -> {
                try {
                    Podatnik p = podatnikDAO.findById(w.getPodatnikId()).orElse(null);
                    Pracownik pr = pracownikDAO.findById(w.getPracownikId()).orElse(null);
                    boolean mp = p != null && (p.getImie()+" "+p.getNazwisko()).toLowerCase().contains(fl);
                    boolean mr = pr != null && (pr.getImie()+" "+pr.getNazwisko()).toLowerCase().contains(fl);
                    if (!(mp||mr)) return false;
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
    }
    @FXML
    private void onFilterMandatyNoAction() {
        // nieużywane, placeholder
    }

    private void loadWizyty() {
        wizytaMaster.clear();
        try {
            List<Wizyta> list = rs.raportWizyt();
            if (CurrentSession.isPodatnik()) {
                list.removeIf(w -> !w.getPodatnikId().equals(CurrentSession.getUserId()));
            }
            wizytaMaster.addAll(list);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać wizyt:\n" + e.getMessage());
        }
    }

    @FXML
    private void onFilterMandatyDummy() {
        // placeholder
    }

    @FXML
    private void onFilterEmpty() {
        // placeholder
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
