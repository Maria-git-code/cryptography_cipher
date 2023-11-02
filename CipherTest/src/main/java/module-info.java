module com.example.ciphertest {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ciphertest to javafx.fxml;
    exports com.example.ciphertest;
}