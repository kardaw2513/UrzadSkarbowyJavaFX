package Controllers;

import Model.Podatnik;
import Model.Pracownik;
import Services.CurrentSession;
import Services.dao.PodatnikDAO;
import Services.dao.PracownikDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class LoginController {
    @FXML private RadioButton rbPodatnik;
    @FXML private RadioButton rbPracownik;
    @FXML private ComboBox<UserItem> userCombo;
    @FXML private Button btnLogin;

    private ToggleGroup roleGroup = new ToggleGroup();
    private final PodatnikDAO podatnikDAO = new PodatnikDAO();
    private final PracownikDAO pracownikDAO = new PracownikDAO();
    private final ObservableList<UserItem> users = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        rbPodatnik.setToggleGroup(roleGroup);
        rbPracownik.setToggleGroup(roleGroup);
        // domyślnie podatnik
        rbPodatnik.setSelected(true);
        loadUsersAsPodatnik();

        roleGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == rbPodatnik) {
                loadUsersAsPodatnik();
            } else {
                loadUsersAsPracownik();
            }
        });
    }

    private void loadUsersAsPodatnik() {
        users.clear();
        try {
            List<Podatnik> list = podatnikDAO.findAll();
            if (list.isEmpty()) {
                // jeśli brak podatników, dodaj domyślnego "Jan Kowalski"
                Podatnik defaultP = new Podatnik("Jan", "Kowalski");
                podatnikDAO.save(defaultP);
                // ponownie pobierz listę
                list = podatnikDAO.findAll();
            }
            for (Podatnik p : list) {
                users.add(new UserItem(p.getId(), p.getImie() + " " + p.getNazwisko()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać/dodać domyślnego podatnika:\n" + e.getMessage());
        }
        userCombo.setItems(users);
        if (!users.isEmpty()) {
            userCombo.setValue(users.get(0));
        } else {
            userCombo.setValue(null);
        }
    }

    private void loadUsersAsPracownik() {
        users.clear();
        try {
            List<Pracownik> list = pracownikDAO.findAll();
            if (list.isEmpty()) {
                // jeśli brak pracowników, dodaj domyślnego "Karol Kozieł"
                Pracownik defaultP = new Pracownik("Karol", "Kozieł");
                pracownikDAO.save(defaultP);
                // ponownie pobierz listę
                list = pracownikDAO.findAll();
            }
            for (Pracownik p : list) {
                users.add(new UserItem(p.getId(), p.getImie() + " " + p.getNazwisko()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się pobrać/dodać domyślnego pracownika:\n" + e.getMessage());
        }
        userCombo.setItems(users);
        if (!users.isEmpty()) {
            userCombo.setValue(users.get(0));
        } else {
            userCombo.setValue(null);
        }
    }

    @FXML
    private void onLogin() {
        UserItem sel = userCombo.getValue();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Wybierz użytkownika do zalogowania.");
            return;
        }
        if (rbPodatnik.isSelected()) {
            CurrentSession.setSession(sel.id, CurrentSession.Role.PODATNIK);
        } else {
            CurrentSession.setSession(sel.id, CurrentSession.Role.PRACOWNIK);
        }
        // zamykamy okno logowania i pokazujemy główny
        try {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.close();
            // Zakładam, że HelloApplication ma statyczną metodę showMainWindow()
            HelloApplication.showMainWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    // klasa pomocnicza do ComboBox
    public static class UserItem {
        public final String id;
        public final String display;
        public UserItem(String id, String display) {
            this.id = id;
            this.display = display;
        }
        @Override public String toString() { return display; }
    }
}
