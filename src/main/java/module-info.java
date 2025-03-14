module com.example.WitW {
    requires javafx.controls;
    requires javafx.fxml;
    requires jsapi;


    opens Wandering to javafx.graphics, javafx.fxml;
    exports Wandering;
}

