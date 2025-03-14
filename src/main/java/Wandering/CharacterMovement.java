package Wandering;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.*;

public class CharacterMovement {
    private int rows;
    private int cols;
    private GridPane gridPane;
    private Cell[][] cells;
    private List<Character> characters = new ArrayList<>();
    private Timeline timeline;
    private int moveCount = 0;
    private int longestRun = 0;
    private int shortestRun = Integer.MAX_VALUE;
    private List<Integer> moveHistory = new ArrayList<>();
    private Stage gameStage;
    private boolean isGrade6to8; // ✅ Variable for 6-8 grade tracking
    private boolean isK2; // ✅ FIX: Add missing variable for K-2 mode

    // ✅ FIX: Modify constructor to accept isGrade6to8 and isK2
    public CharacterMovement(int rows, int cols, int[][] characterPositions, boolean isGrade6to8, boolean isK2) {
        this.rows = rows;
        this.cols = cols;
        this.isGrade6to8 = isGrade6to8; // ✅ Store value for experiment tracking
        this.isK2 = isK2; // ✅ Store value for K-2 fixed settings
        initializeCharacters(characterPositions);
    }

    public void start(Stage stage) {
        this.gameStage = stage;
        System.out.println("Starting CharacterMovement...");

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setGridLinesVisible(true);
        gridPane.setStyle("-fx-border-color: black; -fx-border-width: 2px;");

        cells = new Cell[rows][cols];

        for (int i = 0; i < cols; i++) {
            ColumnConstraints colConst = new ColumnConstraints(50);
            gridPane.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints(50);
            gridPane.getRowConstraints().add(rowConst);
        }

        System.out.println("Creating grid of size " + rows + "x" + cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
                gridPane.add(cells[i][j].stackPane, j, i);
                GridPane.setHalignment(cells[i][j].stackPane, HPos.CENTER);
            }
        }

        System.out.println("Grid successfully created.");

        updateCells();

        Scene scene = new Scene(gridPane, cols * 50, rows * 50);
        stage.setScene(scene);
        stage.setTitle("Wandering in the Woods");
        stage.show();

        System.out.println("GameGrid displayed successfully.");

        timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> moveCharacters()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void moveCharacters() {
        moveCount++;
        System.out.println("Moving characters...");

        for (Character character : characters) {
            character.moveRandomly(rows, cols);
        }

        updateCells();
        checkForMerging();
    }

    private void updateCells() {
        System.out.println("Updating grid cells...");

        for (Character character : characters) {
            if (character.getRow() >= rows || character.getCol() >= cols || character.getRow() < 0 || character.getCol() < 0) {
                System.out.println("ERROR: Character tried to move out of bounds! Resetting position.");
                character.resetPosition(rows, cols);
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j].clear();
            }
        }

        for (Character character : characters) {
            System.out.println("Placing character at (" + character.getRow() + ", " + character.getCol() + ")");
            cells[character.getRow()][character.getCol()].addCharacter(character.groupSize);
        }
    }

    private void checkForMerging() {
        for (int i = 0; i < characters.size(); i++) {
            for (int j = i + 1; j < characters.size(); j++) {
                if (characters.get(i).getRow() == characters.get(j).getRow() &&
                        characters.get(i).getCol() == characters.get(j).getCol()) {

                    System.out.println("Merge detected at (" + characters.get(i).getRow() + "," + characters.get(i).getCol() + ")");

                    moveHistory.add(moveCount);
                    longestRun = Math.max(longestRun, moveCount);
                    shortestRun = Math.min(shortestRun, moveCount);

                    characters.get(i).groupSize += characters.get(j).groupSize;
                    characters.remove(j);
                    j--;

                    moveCount = 0; // Reset move count for next merging phase
                }
            }
        }

        if (characters.size() == 1) {
            System.out.println("All characters merged! Stopping game...");
            timeline.stop();
            showResultsPage();
        }
    }

    private void showResultsPage() {
        System.out.println("Game Over! Displaying results...");

        int sum = moveHistory.stream().mapToInt(Integer::intValue).sum();
        int averageRun = moveHistory.size() > 0 ? sum / moveHistory.size() : 0;
        int totalMoves = moveHistory.stream().mapToInt(Integer::intValue).sum();

        // ✅ Fix: Pass `isK2` as the 8th argument
        ResultsPage resultsPage = new ResultsPage(longestRun, shortestRun, averageRun, totalMoves, rows, cols, isGrade6to8, isK2);
        Stage resultsStage = new Stage();
        resultsPage.start(resultsStage);

        gameStage.close();
    }

    private void initializeCharacters(int[][] positions) {
        for (int[] pos : positions) {
            if (pos[0] >= rows || pos[1] >= cols || pos[0] < 0 || pos[1] < 0) {
                System.out.println("ERROR: Invalid character position " + Arrays.toString(pos) + ". Resetting.");
                pos[0] = Math.min(rows - 1, Math.max(0, pos[0]));
                pos[1] = Math.min(cols - 1, Math.max(0, pos[1]));
            }
            characters.add(new Character(pos[0], pos[1]));
        }
    }



private class Cell {
        StackPane stackPane;

        Cell() {
            Rectangle rect = new Rectangle(40, 40);
            rect.setFill(Color.LIGHTGRAY);
            rect.setStroke(Color.BLACK);
            stackPane = new StackPane(rect);
        }

        void addCharacter(int groupSize) {
            stackPane.getChildren().clear();
            Rectangle rect = new Rectangle(30, 30);
            rect.setFill(getColorByGroupSize(groupSize));
            stackPane.getChildren().add(rect);
        }

        void clear() {
            stackPane.getChildren().clear();
        }

        private Color getColorByGroupSize(int groupSize) {
            switch (groupSize) {
                case 1: return Color.BLUE;
                case 2: return Color.GREEN;
                case 3: return Color.ORANGE;
                case 4: return Color.RED;
                default: return Color.PURPLE;
            }
        }
    }

    public class Character {
        int row, col;
        int groupSize = 1;

        Character(int row, int col) {
            this.row = row;
            this.col = col;
        }

        void moveRandomly(int maxRows, int maxCols) {
            int direction = (int) (Math.random() * 4);
            switch (direction) {
                case 0: if (row > 0) row--; break;
                case 1: if (row < maxRows - 1) row++; break;
                case 2: if (col > 0) col--; break;
                case 3: if (col < maxCols - 1) col++; break;
            }
        }

        void resetPosition(int maxRows, int maxCols) {
            row = Math.max(0, Math.min(row, maxRows - 1));
            col = Math.max(0, Math.min(col, maxCols - 1));
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }
}
