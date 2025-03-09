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
        openGrid(5, 5);
    }

    @FXML
    private void handleLevel3to5Button() {
        openCustomGrid();
    }

    @FXML
    private void handleLevel6to8Button() {
        openCustomGrid();
    }

    private void openGrid(int rows, int cols) {
        GameGrid gameGrid = new GameGrid(rows, cols);
        Stage gridStage = new Stage();
        gameGrid.start(gridStage);
    }

    private void openCustomGrid() {
        GameGrid gameGrid = new GameGrid();
        Stage gridStage = new Stage();
        gameGrid.start(gridStage);
    }

}
