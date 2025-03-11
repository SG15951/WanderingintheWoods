package Wandering;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class GridSizeMenu {
    private VBox characterInputsContainer;
    private boolean isK2Mode = false; // Flag for K-2 level

    public GridSizeMenu(boolean isK2) {
        this.isK2Mode = isK2;
    }

    public void start(Stage stage) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        // Grid Size Inputs (Aligned in a single row)
        HBox gridSizeInputs = new HBox(10);
        gridSizeInputs.setAlignment(Pos.CENTER);

        Label widthLabel = new Label("Width:");
        TextField widthInput = new TextField();
        widthInput.setPrefWidth(50);
        widthInput.setPromptText("3-12");

        Label heightLabel = new Label("Height:");
        TextField heightInput = new TextField();
        heightInput.setPrefWidth(50);
        heightInput.setPromptText("3-12");

        gridSizeInputs.getChildren().addAll(widthLabel, widthInput, heightLabel, heightInput);

        Button createGridButton = new Button("Create Grid");

        // Layout for character selection (only if NOT K-2)
        HBox characterSelectionBox = new HBox(10);
        characterSelectionBox.setAlignment(Pos.CENTER);

        ComboBox<Integer> characterDropdown = new ComboBox<>();
        if (!isK2Mode) {
            Label charLabel = new Label("Characters:");
            characterDropdown.getItems().addAll(2, 3, 4);
            characterDropdown.setValue(2); // Default to 2 characters
            characterSelectionBox.getChildren().addAll(charLabel, characterDropdown);
        }

        // Character input container (not needed for K-2)
        characterInputsContainer = new VBox(5);
        if (!isK2Mode) {
            updateCharacterInputs(2, 5, 5);
            characterDropdown.setOnAction(e -> {
                int selectedCharacterCount = characterDropdown.getValue();
                try {
                    int cols = Integer.parseInt(widthInput.getText());
                    int rows = Integer.parseInt(heightInput.getText());
                    updateCharacterInputs(selectedCharacterCount, rows, cols);
                } catch (NumberFormatException ignored) {
                    updateCharacterInputs(selectedCharacterCount, 5, 5);
                }
            });
        }

        // Button action to create the grid
        createGridButton.setOnAction(e -> {
            if (isK2Mode) {
                System.out.println("K-2 Mode: Characters at (1,1) and (5,5)");
                GameGrid gameGrid = new GameGrid(5, 5);
                Stage gridStage = new Stage();
                gameGrid.start(gridStage);
                stage.close();
            } else {
                try {
                    int cols = Integer.parseInt(widthInput.getText());
                    int rows = Integer.parseInt(heightInput.getText());

                    if (cols < 3 || cols > 12 || rows < 3 || rows > 12) {
                        showAlert("Grid size must be between 3 and 12.");
                        return;
                    }

                    List<String> characterPositions = new ArrayList<>();
                    for (int i = 0; i < characterInputsContainer.getChildren().size(); i++) {
                        HBox inputRow = (HBox) characterInputsContainer.getChildren().get(i);
                        TextField xField = (TextField) inputRow.getChildren().get(1);
                        TextField yField = (TextField) inputRow.getChildren().get(2);

                        int x = Integer.parseInt(xField.getText());
                        int y = Integer.parseInt(yField.getText());

                        if (x < 1 || x > cols || y < 1 || y > rows) {
                            showAlert("Character " + (i + 1) + " position is out of bounds.");
                            return;
                        }

                        characterPositions.add("Character " + (i + 1) + " starts at: (" + x + ", " + y + ")");
                    }

                    System.out.println("Grid Size: " + rows + "x" + cols);
                    characterPositions.forEach(System.out::println);

                    GameGrid gameGrid = new GameGrid(rows, cols);
                    Stage gridStage = new Stage();
                    gameGrid.start(gridStage);
                    stage.close();
                } catch (NumberFormatException ex) {
                    showAlert("Please enter valid numbers.");
                }
            }
        });

        if (!isK2Mode) layout.getChildren().add(new Label("Select Grid Size:"));
        if (!isK2Mode) layout.getChildren().add(gridSizeInputs);
        if (!isK2Mode) layout.getChildren().add(new Label("Select Number of Characters:"));
        if (!isK2Mode) layout.getChildren().add(characterSelectionBox);
        if (!isK2Mode) layout.getChildren().add(characterInputsContainer);
        if (isK2Mode) layout.getChildren().add(new Label("Grid size is 5x5."));
        if (isK2Mode) layout.getChildren().add(new Label("Characters start at 1,1 and 5,5."));
        layout.getChildren().add(createGridButton);

        Scene scene = new Scene(layout, 350, 400);
        stage.setScene(scene);
        stage.setTitle(isK2Mode ? "K-2 Grid Setup" : "Choose Grid Size & Characters");
        stage.show();
    }

    private void updateCharacterInputs(int characterCount, int rows, int cols) {
        characterInputsContainer.getChildren().clear();
        for (int i = 0; i < characterCount; i++) {
            HBox inputRow = new HBox(5);
            inputRow.setAlignment(Pos.CENTER);

            Label label = new Label("Character " + (i + 1) + " position:");
            TextField xInput = new TextField("1");
            xInput.setPrefWidth(40);
            TextField yInput = new TextField("1");
            yInput.setPrefWidth(40);

            inputRow.getChildren().addAll(label, xInput, yInput);
            characterInputsContainer.getChildren().add(inputRow);
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
