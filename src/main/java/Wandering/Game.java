package Wandering;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.*;

public class Game {
    private int moveCount = 0;
    private int shortestMove = Integer.MAX_VALUE;
    private int longestMove = 0;
    private List<Integer> moveHistory = new ArrayList<>();
    private String gradeLevel;

    public Game(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public void recordMove() {
        moveCount++;
    }

    public void checkEncounters(List<CharacterMovement.Character> characters) {
        for (int i = 0; i < characters.size(); i++) {
            for (int j = i + 1; j < characters.size(); j++) {
                if (characters.get(i).getRow() == characters.get(j).getRow() &&
                        characters.get(i).getCol() == characters.get(j).getCol()) {

                    moveHistory.add(moveCount);
                    updateStats();
                    showEncounterMessage();
                    return;
                }
            }
        }
    }

    private void updateStats() {
        shortestMove = Math.min(shortestMove, moveCount);
        longestMove = Math.max(longestMove, moveCount);

        int sum = moveHistory.stream().mapToInt(Integer::intValue).sum();
        int averageMove = moveHistory.isEmpty() ? 0 : sum / moveHistory.size();

    }

    private void showEncounterMessage() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Players Encountered!");
            alert.setContentText("Game over after " + moveCount + " moves!");
            alert.showAndWait();
        });
    }
}
