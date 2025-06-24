module com.urzadskarbowy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens Controllers to javafx.fxml;
    opens Model to javafx.fxml;
}
