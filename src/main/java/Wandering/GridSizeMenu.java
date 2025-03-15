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
    private boolean isGrade6to8;
    private boolean isK2;
    private TextField widthInput, heightInput;
    private ComboBox<Integer> characterDropdown;
    private ComboBox<String> strategyDropdown;
    private int rows, cols;
    private int[][] characterPositions;

    public GridSizeMenu(boolean isK2, boolean isGrade6to8) {
        this.isK2 = isK2;
        this.isGrade6to8 = isGrade6to8;
    }

    public void start(Stage stage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        Label title = new Label(isK2 ? "Grid Size and Characters" : "Select Grid Size and Characters");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label k2Explain = new Label("In 'Wandering in the Woods,' characters randomly move through the grid until they meet. " +
                "Watch them explore and find each other!");
        k2Explain.setWrapText(true);
        k2Explain.setAlignment(Pos.CENTER);
        k2Explain.setMaxWidth(400);

        layout.getChildren().addAll(title, k2Explain);

        if (isK2) {
            // K-2 Mode: Fixed Grid & Characters
            Label fixedGridLabel = new Label("The grid is 5x5.");
            Label fixedCharactersLabel = new Label("The characters start at (1,1) and (5,5).");

            Button createGridButton = new Button("Start Game");
            createGridButton.setOnAction(e -> createGrid(stage, 5, 5, new int[][]{{0, 0}, {4, 4}}, "Random Walk")); // ✅ Default strategy for K-2

            layout.getChildren().addAll(fixedGridLabel, fixedCharactersLabel, createGridButton);
        } else {
            // Grades 3-5 & 6-8 Mode: Custom Grid & Characters
            HBox gridSizeInputs = new HBox(10);
            gridSizeInputs.setAlignment(Pos.CENTER);

            Label widthLabel = new Label("Select Grid Width:");
            widthInput = new TextField();
            widthInput.setPrefWidth(50);
            widthInput.setPromptText("3-12");

            Label heightLabel = new Label("Select Grid Height:");
            heightInput = new TextField();
            heightInput.setPrefWidth(50);
            heightInput.setPromptText("3-12");

            gridSizeInputs.getChildren().addAll(widthLabel, widthInput, heightLabel, heightInput);

            // Character Selection
            HBox characterSelectionBox = new HBox(10);
            characterSelectionBox.setAlignment(Pos.CENTER);
            characterDropdown = new ComboBox<>();
            characterDropdown.getItems().addAll(2, 3, 4);
            characterDropdown.setValue(2);

            Label charLabel = new Label("Number of Characters:");
            characterSelectionBox.getChildren().addAll(charLabel, characterDropdown);

            // Character Input Container
            characterInputsContainer = new VBox(5);
            updateCharacterInputs(2, 5, 5);
            characterDropdown.setOnAction(e -> updateCharacterInputFields());

            // Add a dropdown for movement strategies in 6-8 mode
            HBox strategySelectionBox = new HBox(10);
            strategySelectionBox.setAlignment(Pos.CENTER);
            Label strategyLabel = new Label("Choose a Wandering Protocol:");
            strategyDropdown = new ComboBox<>();
            strategyDropdown.getItems().addAll("Random Walk", "Edge Circling", "Center Circling", "Spiral Movement");
            strategyDropdown.setValue("Random Walk"); // Default
            strategySelectionBox.getChildren().addAll(strategyLabel, strategyDropdown);

            Button createGridButton = new Button("Start Game");
            createGridButton.setOnAction(e -> createGrid(stage));

            layout.getChildren().addAll(new Label("Grid Size:"), gridSizeInputs,
                    new Label("Select Number of Characters:"), characterSelectionBox,new Label("Choose Character Starting Positions:"), characterInputsContainer);

            if (isGrade6to8) {
                layout.getChildren().add(new Label("Select Movement Strategy:"));
                layout.getChildren().add(strategySelectionBox);
            }

            layout.getChildren().add(createGridButton);
        }

        Scene scene = new Scene(layout, 400, isGrade6to8 ? 500 : 450);
        stage.setScene(scene);
        stage.setTitle(isGrade6to8 ? "Grid Experiment (6-8)" : "Choose Grid Size & Characters");
        stage.show();
    }

    private void updateCharacterInputFields() {
        int characterCount = characterDropdown.getValue();
        try {
            rows = Integer.parseInt(heightInput.getText());
            cols = Integer.parseInt(widthInput.getText());
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

            Label label = new Label("Character " + (i + 1) + " starting position:");
            TextField xInput = new TextField("1");
            xInput.setPrefWidth(40);
            TextField yInput = new TextField("1");
            yInput.setPrefWidth(40);

            inputRow.getChildren().addAll(label, xInput, yInput);
            characterInputsContainer.getChildren().add(inputRow);
        }
    }

    private void createGrid(Stage stage) {
        try {
            cols = Integer.parseInt(widthInput.getText());
            rows = Integer.parseInt(heightInput.getText());

            if (cols < 3 || cols > 12 || rows < 3 || rows > 12) {
                showAlert("Grid size must be between 3 and 12.");
                return;
            }

            characterPositions = getCharacterPositions(rows, cols);
            if (characterPositions == null) {
                showAlert("Invalid character positions.");
                return;
            }

            String selectedStrategy = strategyDropdown.getValue();

            CharacterMovement characterMovement = new CharacterMovement(rows, cols, characterPositions, isGrade6to8, isK2, selectedStrategy);
            Stage gameStage = new Stage();
            gameStage.setOnShown(e -> stage.close());
            characterMovement.start(gameStage);
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers.");
        }
    }

    private void createGrid(Stage stage, int rows, int cols, int[][] characterPositions, String strategy) {
        CharacterMovement characterMovement = new CharacterMovement(rows, cols, characterPositions, false, true, strategy);
        Stage gameStage = new Stage();
        gameStage.setOnShown(e -> stage.close());
        characterMovement.start(gameStage);
    }

    private int[][] getCharacterPositions(int rows, int cols) {
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
