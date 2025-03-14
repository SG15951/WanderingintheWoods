package Wandering;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {

    @FXML private Button k2Button;
    @FXML private Button level3to5Button;
    @FXML private Button level6to8Button;

    @FXML
    private void handleK2Button() {
        GridSizeMenu gridSizeMenu = new GridSizeMenu(true, false);
        Stage menuStage = new Stage();
        gridSizeMenu.start(menuStage);
    }

    @FXML
    private void handleLevel3to5Button() {
        GridSizeMenu gridSizeMenu = new GridSizeMenu(false, false);
        Stage menuStage = new Stage();
        gridSizeMenu.start(menuStage);
    }

    @FXML
    private void handleLevel6to8Button() {
        GridSizeMenu gridSizeMenu = new GridSizeMenu(false, true);
        Stage menuStage = new Stage();
        gridSizeMenu.start(menuStage);
    }

}
