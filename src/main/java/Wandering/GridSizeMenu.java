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
    private boolean isK2Mode;
    private TextField widthInput, heightInput;
    private ComboBox<Integer> characterDropdown;

    public GridSizeMenu(boolean isK2) {
        this.isK2Mode = isK2;
    }

    public void start(Stage stage) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        // Grid Size Inputs
        HBox gridSizeInputs = new HBox(10);
        gridSizeInputs.setAlignment(Pos.CENTER);

        Label widthLabel = new Label("Width:");
        widthInput = new TextField();
        widthInput.setPrefWidth(50);
        widthInput.setPromptText("3-12");

        Label heightLabel = new Label("Height:");
        heightInput = new TextField();
        heightInput.setPrefWidth(50);
        heightInput.setPromptText("3-12");

        gridSizeInputs.getChildren().addAll(widthLabel, widthInput, heightLabel, heightInput);

        // Character Selection (Only for non-K2)
        HBox characterSelectionBox = new HBox(10);
        characterSelectionBox.setAlignment(Pos.CENTER);
        characterDropdown = new ComboBox<>();

        if (!isK2Mode) {
            Label charLabel = new Label("Characters:");
            characterDropdown.getItems().addAll(2, 3, 4);
            characterDropdown.setValue(2);
            characterSelectionBox.getChildren().addAll(charLabel, characterDropdown);
        }

        // Character Input Container
        characterInputsContainer = new VBox(5);
        if (!isK2Mode) {
            updateCharacterInputs(2, 5, 5);
            characterDropdown.setOnAction(e -> updateCharacterInputFields());
        }

        Button createGridButton = new Button("Create Grid");
        createGridButton.setOnAction(e -> createGrid(stage));

        // Add Elements to Layout
        if (!isK2Mode) {
            layout.getChildren().addAll(new Label("Select Grid Size:"), gridSizeInputs,
                    new Label("Select Number of Characters:"), characterSelectionBox,
                    characterInputsContainer);
        } else {
            layout.getChildren().addAll(new Label("Grid size is 5x5."), new Label("Characters start at 1,1 and 5,5."));
        }

        layout.getChildren().add(createGridButton);

        Scene scene = new Scene(layout, 350, 400);
        stage.setScene(scene);
        stage.setTitle(isK2Mode ? "K-2 Grid Setup" : "Choose Grid Size & Characters");
        stage.show();
    }

    private void updateCharacterInputFields() {
        int characterCount = characterDropdown.getValue();
        try {
            int rows = Integer.parseInt(heightInput.getText());
            int cols = Integer.parseInt(widthInput.getText());
            updateCharacterInputs(characterCount, rows, cols);
        } catch (NumberFormatException ignored) {
            updateCharacterInputs(characterCount, 5, 5);
        }
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

    private void createGrid(Stage stage) {
        int rows = 5, cols = 5; // Default for K2 Mode

        if (!isK2Mode) {
            try {
                cols = Integer.parseInt(widthInput.getText());
                rows = Integer.parseInt(heightInput.getText());

                if (cols < 3 || cols > 12 || rows < 3 || rows > 12) {
                    showAlert("Grid size must be between 3 and 12.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Please enter valid numbers.");
                return;
            }
        }

        int[][] characterPositions = getCharacterPositions(rows, cols);
        if (characterPositions == null) {
            showAlert("Invalid character positions.");
            return;
        }

        CharacterMovement characterMovement = new CharacterMovement(rows, cols, characterPositions);
        Stage gameStage = new Stage();
        characterMovement.start(gameStage);
        stage.close();
    }

    private int[][] getCharacterPositions(int rows, int cols) {
        if (isK2Mode) {
            return new int[][]{{0, 0}, {4, 4}}; // Fixed positions for K2
        }

        int characterCount = characterDropdown.getValue();
        int[][] positions = new int[characterCount][2];

        for (int i = 0; i < characterInputsContainer.getChildren().size(); i++) {
            HBox inputRow = (HBox) characterInputsContainer.getChildren().get(i);
            TextField xField = (TextField) inputRow.getChildren().get(1);
            TextField yField = (TextField) inputRow.getChildren().get(2);

            try {
                int x = Integer.parseInt(xField.getText()) - 1;
                int y = Integer.parseInt(yField.getText()) - 1;

                if (x < 0 || x >= cols || y < 0 || y >= rows) {
                    return null;
                }

                positions[i][0] = x;
                positions[i][1] = y;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return positions;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
