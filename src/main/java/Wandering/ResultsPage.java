package Wandering;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class ResultsPage {
    private int longestRun;
    private int shortestRun;
    private int averageRun;
    private int totalMoves;
    private int rows;
    private int cols;
    private boolean isGrade6to8;
    private boolean isK2;
    private TextSpeech textSpeech;
    private String movementStrategy;
    private static Map<String, Integer> gridTimeTracker = new HashMap<>();
    private static Map<String, Integer> protocolBestTimes = new HashMap<>();

    public ResultsPage(int longestRun, int shortestRun, int averageRun, int totalMoves, int rows, int cols, boolean isGrade6to8, boolean isK2, String movementStrategy) {
        this.longestRun = longestRun;
        this.shortestRun = shortestRun;
        this.averageRun = averageRun;
        this.totalMoves = totalMoves;
        this.rows = rows;
        this.cols = cols;
        this.isGrade6to8 = isGrade6to8;
        this.isK2 = isK2;
        this.movementStrategy = movementStrategy;

        try {
            this.textSpeech = new TextSpeech("Game Results"); // Ensure textSpeech is initialized
        } catch (Exception e) {
            System.err.println("TTS Initialization Failed: " + e.getMessage());
            this.textSpeech = null; // Prevent crashes if TTS fails
        }

        // Track protocol performance in grades 6-8
        if (isGrade6to8) {
            protocolBestTimes.put(movementStrategy, totalMoves);

            String gridKey = rows + "x" + cols;
            gridTimeTracker.put(gridKey, totalMoves);
        }
    }

    public void start(Stage stage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Game Over! Here are your results:");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label longestRunLabel = new Label("Longest Run Without Meeting: " + longestRun);
        Label shortestRunLabel = new Label("Shortest Run Before Meeting: " + shortestRun);
        Label averageRunLabel = new Label("Average Moves Before Meeting: " + averageRun);
        Label totalMovesLabel = new Label("Total Moves Taken: " + totalMoves);
        Label gridSizeLabel = new Label("Grid Size: " + rows + " x " + cols);

        layout.getChildren().addAll(title, longestRunLabel, shortestRunLabel, averageRunLabel, totalMovesLabel, gridSizeLabel);

        // Show tracking results only for grades 6-8
        if (isGrade6to8) {
            String shortestGridSize = getShortestTimeGrid();
            String longestGridSize = getLongestTimeGrid();
            String bestProtocol = getBestProtocol(); // ✅ Retrieve best protocol here

            Label experimentLabel = new Label("Experimental Data:");
            experimentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: darkred;");

            Label shortestGridLabel = new Label("Grid with Fastest Completion: " + shortestGridSize);
            Label longestGridLabel = new Label("Grid with Longest Completion: " + longestGridSize);
            Label bestProtocolLabel = new Label("Best Wandering Protocol: " + bestProtocol); // ✅ Moved inside experiment section

            layout.getChildren().addAll(experimentLabel, shortestGridLabel, longestGridLabel, bestProtocolLabel);
        }

        // "Back to Grid Menu" button
        Button backButton = new Button("Back to Grid Menu");
        backButton.setOnAction(e -> {
            stage.close();
            Stage gridMenuStage = new Stage();
            new GridSizeMenu(isK2, isGrade6to8).start(gridMenuStage);
        });

        layout.getChildren().add(backButton);

        Scene scene = new Scene(layout, 400, isGrade6to8 ? 400 : 350);
        stage.setScene(scene);
        stage.setTitle("Results");
        stage.show();

        // Wait to speak results after display updates
        Platform.runLater(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String speechText = "Game Over! Longest run without meeting was " + longestRun +
                    " moves. Shortest run before meeting was " + shortestRun +
                    " moves. The average number of moves before meeting was " + averageRun +
                    " moves. The total moves taken were " + totalMoves + " moves. The wandering protocol used was " + movementStrategy + ".";

            if (isGrade6to8) {
                speechText += " The best protocol so far was " + getBestProtocol() + ".";
            }

            // Only call TTS if it was successfully initialized
            if (textSpeech != null) {
                textSpeech.speakText(speechText);
            } else {
                System.err.println("TTS is null, skipping speech output.");
            }
        });
    }

    private String getShortestTimeGrid() {
        return gridTimeTracker.isEmpty() ? "N/A" : gridTimeTracker.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private String getLongestTimeGrid() {
        return gridTimeTracker.isEmpty() ? "N/A" : gridTimeTracker.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    private String getBestProtocol() {
        return protocolBestTimes.entrySet().stream()
                .min(Map.Entry.comparingByValue()) // Get the protocol with the lowest total moves
                .map(Map.Entry::getKey)
                .orElse("N/A"); // If no data, return "N/A"
    }

    public void closeTextSpeech() {
        textSpeech.close();
    }
}
