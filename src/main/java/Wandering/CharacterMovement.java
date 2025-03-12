package Wandering;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

public class CharacterMovement {

    private int rows;
    private int cols;
    private GridPane gridPane;
    private Cell[][] cells;
    private List<Character> characters = new ArrayList<>();
    private int moveCount = 0;
    private Timeline timeline;

    // Constructor to initialize grid size and character positions
    public CharacterMovement(int rows, int cols, int[][] characterPositions) {
        this.rows = rows;
        this.cols = cols;
        initializeCharacters(characterPositions);
    }

    // Method to set up and start the game
    public void start(Stage stage) {
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        cells = new Cell[rows][cols];

        // Initialize cells of the grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
                gridPane.add(cells[i][j].stackPane, j, i);
            }
        }

        updateCells();

        // Timeline to move characters periodically
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> moveCharacters()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        VBox layout = new VBox(10, gridPane);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, cols * 50 + 50, rows * 50 + 50);
        stage.setScene(scene);
        stage.setTitle("Wandering in the Woods");
        stage.show();
    }

    // Move all characters randomly and update grid
    private void moveCharacters() {
        for (Character character : characters) {
            character.moveRandomly(rows, cols);
        }

        moveCount++;
        updateCells();
        checkEncounters();
    }

    // Refresh cells visually
    private void updateCells() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j].clear();
            }
        }

        for (Character character : characters) {
            cells[character.row][character.col].addCharacter(character.groupSize);
        }
    }

    // Check if any characters have encountered each other
    private void checkEncounters() {
        Map<String, List<Character>> positionMap = new HashMap<>();
        for (Character character : characters) {
            String pos = character.row + "," + character.col;
            positionMap.putIfAbsent(pos, new ArrayList<>());
            positionMap.get(pos).add(character);
        }

        boolean merged = false;
        for (List<Character> group : positionMap.values()) {
            if (group.size() > 1) {
                merged = true;
                Character mergedCharacter = group.get(0);

                for (int i = 1; i < group.size(); i++) {
                    mergedCharacter.groupSize += group.get(i).groupSize;
                    characters.remove(group.get(i));
                }
            }
        }

        if (merged) {
            if (characters.size() == 1) {
                timeline.stop();
                showEncounterMessage();
            }
        }
    }

    // Display visual and alert message upon encounter
    private void showEncounterMessage() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("All Characters Joined!");
            alert.setContentText("All characters joined after " + moveCount + " moves!");
            alert.showAndWait();
        });
    }

    // Initialize character positions and check for duplicates
    private void initializeCharacters(int[][] positions) {
        Set<String> initialPositions = new HashSet<>();
        for (int[] pos : positions) {
            String key = pos[0] + "," + pos[1];
            if (initialPositions.contains(key)) {
                throw new IllegalArgumentException("Characters cannot start at the same position.");
            }
            initialPositions.add(key);
            characters.add(new Character(pos[0], pos[1]));
        }
    }

    // Cell class represents each cell in the grid
    private class Cell {
        StackPane stackPane;

        Cell() {
            Rectangle rect = new Rectangle(40, 40);
            rect.setFill(Color.LIGHTGRAY);
            rect.setStroke(Color.BLACK);
            stackPane = new StackPane(rect);
        }

        // Add visual representation of a character
        void addCharacter(int groupSize) {
            Rectangle rect = new Rectangle(30, 30);
            rect.setFill(groupSize > 1 ? Color.PURPLE : Color.BLUE); // Purple if merged
            stackPane.getChildren().add(rect);
        }

        // Clear the character from the cell
        void clear() {
            stackPane.getChildren().removeIf(node -> node instanceof Rectangle && ((Rectangle) node).getWidth() == 30);
        }
    }

    // Character class to handle character movements
    private class Character {
        int row, col;
        int groupSize = 1;

        Character(int row, int col) {
            this.row = row;
            this.col = col;
        }

        // Move character randomly within grid bounds
        void moveRandomly(int maxRows, int maxCols) {
            int direction = (int) (Math.random() * 4);
            switch (direction) {
                case 0: if (row > 0) row--; break;
                case 1: if (row < maxRows - 1) row++; break;
                case 2: if (col > 0) col--; break;
                case 3: if (col < maxCols - 1) col++; break;
            }
        }
    }
}
