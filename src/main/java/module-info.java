module com.example.WitW {
    requires javafx.controls;
    requires javafx.fxml;
    requires jsapi;


    opens com.example.WitW to javafx.fxml;
    exports com.example.WitW;

    opens Wandering to javafx.graphics, javafx.fxml;
    exports Wandering;
}

