package Wandering;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameGrid {

    private int rows = 5;  // Default size
    private int cols = 5;  // Default size
    private boolean lockedGrid = false;  // Flag to lock grid size

    public GameGrid() {}  // Default constructor (user can change grid)

    public GameGrid(int rows, int cols) {  // Constructor for locked grid
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

        // Generate initial grid
        generateGrid(gridPane);

        if (!lockedGrid) {  // Only show input fields and button for custom grids
            TextField widthInput = new TextField();
            widthInput.setPromptText("Width (3-12)");
            TextField heightInput = new TextField();
            heightInput.setPromptText("Height (3-12)");

            Button createGridButton = new Button("Create Grid");

            createGridButton.setOnAction(e -> {
                try {
                    int inputCols = Integer.parseInt(widthInput.getText());
                    int inputRows = Integer.parseInt(heightInput.getText());

                    // Ensure valid size
                    if (inputCols < 3 || inputCols > 12 || inputRows < 3 || inputRows > 12) {
                        showAlert("Size must be between 3 and 12.");
                        return;
                    }

                    this.cols = inputCols;
                    this.rows = inputRows;
                    generateGrid(gridPane);
                } catch (NumberFormatException ex) {
                    showAlert("Please enter valid numbers.");
                }
            });

            layout.getChildren().addAll(widthInput, heightInput, createGridButton);
        }

        layout.getChildren().add(gridPane);

        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);
        stage.setTitle(lockedGrid ? "Fixed 5x5 Grid" : "Custom Game Grid");
        stage.show();
    }

    private void generateGrid(GridPane gridPane) {
        gridPane.getChildren().clear();  // Clear previous grid

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
