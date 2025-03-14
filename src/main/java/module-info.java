module demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires jsapi;

    opens Wandering to javafx.fxml;
    exports Wandering;
}


