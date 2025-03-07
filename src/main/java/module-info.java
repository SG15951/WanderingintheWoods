module com.example.WitW {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.WitW to javafx.fxml;
    exports com.example.WitW;
}