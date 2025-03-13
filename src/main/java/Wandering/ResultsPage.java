package Wandering;

import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import com.example.WitW.TextSpeech;

public class ResultsPage {
    private String gradeLevel;  // Change to String (K-2, 3-5, 6-8)
    private List<String> movements;
    private List<String> stats;
    private List<String> playerStats;
    private TextSpeech textSpeech;

    public ResultsPage(String gradeLevel, List<String> movements, List<String> stats, List<String> playerStats) {
        this.gradeLevel = gradeLevel;
        this.movements = movements;
        this.stats = stats;
        this.playerStats = playerStats;

        // Initialize TextSpeech with the gradeLevel
        this.textSpeech = new TextSpeech(gradeLevel);
    }

    public void start(Stage stage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Title
        Label title = new Label("Game Results");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Movement History
        VBox movementBox = new VBox(5);
        for (String move : movements) {
            movementBox.getChildren().add(new Label(move));
        }

        // Game Statistics
        VBox statsBox = new VBox(5);
        for (String stat : stats) {
            statsBox.getChildren().add(new Label(stat));
        }

        // Player Stats (up to 4 players)
        VBox playerStatsBox = new VBox(5);
        for (int i = 0; i < Math.min(playerStats.size(), 4); i++) {
            playerStatsBox.getChildren().add(new Label("Player " + (i + 1) + ": " + playerStats.get(i)));
        }

        layout.getChildren().addAll(title, new Label("Player Movements:"), movementBox, new Label("Statistics:"), statsBox, new Label("Player Stats:"), playerStatsBox);

        // Grade-specific messages
        switch (gradeLevel) {
            case "K-2": // K-2
                layout.getChildren().add(new Label("Great job! You explored the grid!"));
                layout.getChildren().add(new Label("Total Moves: " + movements.size() + " moves"));
                layout.getChildren().add(new Label("You made it with " + movements.size() + " moves!"));
                textSpeech.speakText("Great job! You explored the grid with a total of " + movements.size() + " moves!");
                break;
            case "3-5": // 3-5
                layout.getChildren().add(new Label("Game Results:"));
                layout.getChildren().add(new Label("Total Moves: " + movements.size()));
                layout.getChildren().add(new Label("Longest Run: " + stats.get(0)));  // Assume stats.get(0) is longest run
                layout.getChildren().add(new Label("Shortest Run: " + stats.get(1)));  // Assume stats.get(1) is shortest run
                layout.getChildren().add(new Label("Average Moves: " + stats.get(2))); // Assume stats.get(2) is average moves
                textSpeech.speakText("Game results: Total Moves: " + movements.size() + ", Longest Run: " + stats.get(0) + ", Shortest Run: " + stats.get(1) + ", Average Moves: " + stats.get(2));
                break;
            case "6-8": // 6-8
                layout.getChildren().add(new Label("Detailed Game Stats:"));
                layout.getChildren().add(new Label("Total Moves: " + movements.size()));
                layout.getChildren().add(new Label("Longest Run: " + stats.get(0)));
                layout.getChildren().add(new Label("Shortest Run: " + stats.get(1)));
                layout.getChildren().add(new Label("Average Moves: " + stats.get(2)));
                layout.getChildren().add(new Label("Strategize your next game to optimize your moves!"));
                textSpeech.speakText("Detailed game stats: Total Moves: " + movements.size() + ", Longest Run: " + stats.get(0) + ", Shortest Run: " + stats.get(1) + ", Average Moves: " + stats.get(2));
                break;
            default:
                layout.getChildren().add(new Label("Game complete!"));
                textSpeech.speakText("Game complete!");
        }

        // Add the "Back to Menu" Button at the bottom
        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            MenuApp menuApp = new MenuApp();
            Stage menuStage = new Stage();
            try {
                menuApp.start(menuStage); // Start the main menu
            } catch (Exception ex) {
                ex.printStackTrace(); // Print the exception stack trace for debugging
                // You can also show an alert to the user if needed
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred while starting the main menu");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }

            stage.close(); // Close the results page
        });

        // Add the button at the bottom of the layout
        layout.getChildren().add(backButton);

        // Set the scene and show the stage
        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Results");
        stage.show();
    }

    // Close the TTS when done
    public void closeTextSpeech() {
        textSpeech.close();
    }
}