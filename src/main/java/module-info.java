module com.urzadskarbowy {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // JDBC
    requires java.sql;
    // SQLite JDBC driver - automatyczny moduł (zależnie od wersji jar może mieć automatyczną nazwę)
    requires org.xerial.sqlitejdbc;

    // Jeśli w FXML bezpośrednio odwołujesz się do klas modelu (rzadko się zdarza),
    // można otworzyć pakiet Model również do javafx.fxml.
    opens Controllers to javafx.fxml;
    opens Model to javafx.fxml;

    // Jeśli ktokolwiek poza modułem będzie używał klasy z tych pakietów, eksportujemy:
    exports Controllers;
    exports Model;
    // Pakiety Services/Services.dao zwykle nie są potrzebne do otwarcia do FXMLLoader,
    // bo kontrolery odwołują się do DAO/Service przez kod, a nie w FXML bezpośrednio.
    // Jeśli jednak potrzebujesz otworzyć np. dla testów/refleksji:
    // opens Services to java.base; // zwykle nie
}
