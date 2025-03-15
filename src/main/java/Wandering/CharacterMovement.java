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
    private boolean isGrade6to8;
    private boolean isK2;
    private String movementStrategy;

    public CharacterMovement(int rows, int cols, int[][] characterPositions, boolean isGrade6to8, boolean isK2, String movementStrategy) {
        this.rows = rows;
        this.cols = cols;
        this.isGrade6to8 = isGrade6to8;
        this.isK2 = isK2;
        this.movementStrategy = movementStrategy;
        initializeCharacters(characterPositions);
    }

    public void start(Stage stage) {
        this.gameStage = stage;

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

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
                gridPane.add(cells[i][j].stackPane, j, i);
                GridPane.setHalignment(cells[i][j].stackPane, HPos.CENTER);
            }
        }

        updateCells();

        Scene scene = new Scene(gridPane, cols * 50, rows * 50);
        stage.setScene(scene);
        stage.setTitle("Wandering in the Woods");
        stage.show();

        timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> moveCharacters()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void moveCharacters() {
        moveCount++;

        for (Character character : characters) {
            switch (movementStrategy) {
                case "Random Walk":
                    character.moveRandomly(rows, cols);
                    break;
                case "Edge Circling":
                    moveTowardEdges(character);
                    break;
                case "Center Circling":
                    moveTowardCenter(character);
                    break;
                case "Spiral Movement":
                    moveInSpiral(character);
                    break;
            }
        }

        updateCells();
        checkForMerging();
    }

    // Move toward the edges, then start circling them
    private void moveTowardEdges(Character character) {
        if (character.row == 0 || character.row == rows - 1 || character.col == 0 || character.col == cols - 1) {
            // Already at the edge → Start circling
            moveAroundEdges(character);
        } else {
            // Move toward the closest edge
            if (character.row < rows / 2) {
                character.row++;
            } else {
                character.row--;
            }

            if (character.col < cols / 2) {
                character.col++;
            } else {
                character.col--;
            }
        }
    }

    // Move in a circular pattern around the edges
    private void moveAroundEdges(Character character) {
        Random random = new Random();

        // 5% chance to stop moving for a turn
        if (random.nextDouble() < 0.05) {
            return;
        }

        // 10% chance to reverse direction
        boolean reverseDirection = random.nextDouble() < 0.10;

        if (reverseDirection) {
            // Move counterclockwise along the edges
            if (character.row == 0 && character.col > 0) {
                character.col--; // Move left along the top
            } else if (character.col == 0 && character.row < rows - 1) {
                character.row++; // Move down along the left side
            } else if (character.row == rows - 1 && character.col < cols - 1) {
                character.col++; // Move right along the bottom
            } else if (character.col == cols - 1 && character.row > 0) {
                character.row--; // Move up along the right side
            }
        } else {
            // Move clockwise along the edges (default behavior)
            if (character.row == 0 && character.col < cols - 1) {
                character.col++; // Move right along the top
            } else if (character.col == cols - 1 && character.row < rows - 1) {
                character.row++; // Move down along the right side
            } else if (character.row == rows - 1 && character.col > 0) {
                character.col--; // Move left along the bottom
            } else if (character.col == 0 && character.row > 0) {
                character.row--; // Move up along the left side
            }
        }
    }


    // Move toward the center, then orbit around it
    private void moveTowardCenter(Character character) {
        int centerRow = rows / 2;
        int centerCol = cols / 2;

        if (Math.abs(character.row - centerRow) <= 1 && Math.abs(character.col - centerCol) <= 1) {
            // Close enough to center → Start circling
            moveAroundCenter(character);
        } else {
            // Move toward center
            if (character.row < centerRow) {
                character.row++;
            } else if (character.row > centerRow) {
                character.row--;
            }

            if (character.col < centerCol) {
                character.col++;
            } else if (character.col > centerCol) {
                character.col--;
            }
        }
    }

    // Orbit around the center point
    private void moveAroundCenter(Character character) {
        int centerRow = rows / 2;
        int centerCol = cols / 2;
        Random random = new Random();

        // Randomly decide whether to stop (5% chance)
        if (random.nextDouble() < 0.05) {
            return; // Character does nothing this turn
        }

        // Randomly decide whether to reverse direction (10% chance)
        boolean reverseDirection = random.nextDouble() < 0.10;

        // Determine current position relative to the center
        boolean atTop = character.row == centerRow - 1;
        boolean atBottom = character.row == centerRow + 1;
        boolean atLeft = character.col == centerCol - 1;
        boolean atRight = character.col == centerCol + 1;

        if (reverseDirection) {
            // Move in a counterclockwise pattern
            if (atTop && !atLeft) {
                character.col--; // Move left
            } else if (atLeft && !atBottom) {
                character.row++; // Move down
            } else if (atBottom && !atRight) {
                character.col++; // Move right
            } else if (atRight && !atTop) {
                character.row--; // Move up
            }
        } else {
            // Move in the normal clockwise pattern
            if (atTop && !atRight) {
                character.col++; // Move right
            } else if (atRight && !atBottom) {
                character.row++; // Move down
            } else if (atBottom && !atLeft) {
                character.col--; // Move left
            } else if (atLeft && !atTop) {
                character.row--; // Move up
            }
        }
    }

    // Move in a spiral (outward if near center, inward if near edges)
    private void moveInSpiral(Character character) {
        int centerRow = rows / 2;
        int centerCol = cols / 2;
        Random random = new Random();

        // Prevent characters from swapping back and forth
        if (random.nextDouble() < 0.05) {
            return; // 5% chance to stop moving for a turn
        }

        // Track previous position to prevent oscillation
        int previousRow = character.row;
        int previousCol = character.col;

        if (character.row == centerRow && character.col == centerCol) {
            // If exactly in the center, start moving outward
            character.col++;
        } else {
            // Spiral logic
            if (character.row <= centerRow && character.col < centerCol) {
                character.col++; // Move right
            } else if (character.col >= centerCol && character.row < centerRow) {
                character.row++; // Move down
            } else if (character.row >= centerRow && character.col > centerCol) {
                character.col--; // Move left
            } else if (character.col <= centerCol && character.row > centerRow) {
                character.row--; // Move up
            }
        }

        // Prevent swapping back and forth between two squares
        if (character.row == previousRow && character.col == previousCol) {
            character.row++; // Force movement forward if stuck
        }
    }


    private void updateCells() {

        for (Character character : characters) {
            if (character.getRow() >= rows || character.getCol() >= cols || character.getRow() < 0 || character.getCol() < 0) {
                character.resetPosition(rows, cols);
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j].clear();
            }
        }

        for (Character character : characters) {
            cells[character.getRow()][character.getCol()].addCharacter(character.groupSize);
        }
    }

    private void checkForMerging() {
        for (int i = 0; i < characters.size(); i++) {
            for (int j = i + 1; j < characters.size(); j++) {
                if (characters.get(i).getRow() == characters.get(j).getRow() &&
                        characters.get(i).getCol() == characters.get(j).getCol()) {

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
            timeline.stop();
            showResultsPage();
        }
    }

    private void showResultsPage() {
        System.out.println("Game Over! Displaying results...");

        int sum = moveHistory.stream().mapToInt(Integer::intValue).sum();
        int averageRun = moveHistory.size() > 0 ? sum / moveHistory.size() : 0;
        int totalMoves = moveHistory.stream().mapToInt(Integer::intValue).sum();

        ResultsPage resultsPage = new ResultsPage(longestRun, shortestRun, averageRun, totalMoves, rows, cols, isGrade6to8, isK2, movementStrategy);
        Stage resultsStage = new Stage();
        resultsPage.start(resultsStage);

        gameStage.close();
    }

    private void initializeCharacters(int[][] positions) {
        for (int[] pos : positions) {
            if (pos[0] >= rows || pos[1] >= cols || pos[0] < 0 || pos[1] < 0) {
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
