module com.example.urzadslarbowygui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.urzadslarbowygui to javafx.fxml;
    exports com.example.urzadslarbowygui;
}