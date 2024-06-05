module org.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens org.example.javafx to javafx.fxml;
    exports org.example.javafx;
}