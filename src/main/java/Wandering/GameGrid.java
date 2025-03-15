package Wandering;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameGrid {
    private int rows;
    private int cols;
    private boolean lockedGrid;

    public GameGrid() {
        this.rows = 5;  // Default size
        this.cols = 5;  // Default size
        this.lockedGrid = false;
    }

    public GameGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.lockedGrid = true; // Lock grid size
    }

    public void start(Stage stage) {

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setStyle("-fx-border-color: black; -fx-border-width: 2px;");

        // Generate and display the grid properly
        generateGrid(gridPane);

        if (!lockedGrid) {
            TextField widthInput = new TextField();
            widthInput.setPromptText("Width (3-12)");
            TextField heightInput = new TextField();
            heightInput.setPromptText("Height (3-12)");

            Button createGridButton = new Button("Create Grid");

            createGridButton.setOnAction(e -> {
                try {
                    int inputCols = Integer.parseInt(widthInput.getText());
                    int inputRows = Integer.parseInt(heightInput.getText());

                    if (inputCols < 3 || inputCols > 12 || inputRows < 3 || inputRows > 12) {
                        showAlert("Size must be between 3 and 12.");
                        return;
                    }

                    this.cols = inputCols;
                    this.rows = inputRows;
                    generateGrid(gridPane);
                    gridPane.setPrefSize(inputCols * 40 + 50, inputRows * 40 + 50);
                    stage.setScene(new Scene(layout, cols * 50 + 50, rows * 50 + 50));
                } catch (NumberFormatException ex) {
                    showAlert("Please enter valid numbers.");
                }
            });

            System.out.println("GridPane child count: " + gridPane.getChildren().size());
            layout.getChildren().addAll(widthInput, heightInput, createGridButton);
        }

        layout.getChildren().add(gridPane);

        int sceneWidth = cols * 50 + 50;
        int sceneHeight = rows * 50 + 50;

        Scene scene = new Scene(layout, sceneWidth, sceneHeight);
        stage.setScene(scene);
        stage.setTitle(lockedGrid ? "Fixed 5x5 Grid" : "Custom Game Grid");
        stage.show();
    }

    private void generateGrid(GridPane gridPane) {
        gridPane.getChildren().clear();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Rectangle cell = new Rectangle(40, 40, Color.LIGHTGRAY);
                cell.setStroke(Color.BLACK);
                gridPane.add(cell, j, i);
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
