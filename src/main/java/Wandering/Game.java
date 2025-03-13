package Wandering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.stage.Stage;

public class Game {
    private List<String> playerStats = new ArrayList<>();
    private int moveCount = 0;
    private int shortestMove = Integer.MAX_VALUE;
    private int longestMove = 0;
    private Random random = new Random();
    private int player1X, player1Y;
    private int player2X, player2Y;
    private int player3X, player3Y;
    private int player4X, player4Y;
    private static final int GRID_SIZE = 10;
    private List<Integer> moveHistory = new ArrayList<>();
    private String gradeLevel;

    public Game(String gradeLevel, Stage stage) {
        this.gradeLevel = gradeLevel;
        initializeGame();
    }

    /**
     * Initializes the game, resetting player positions and stats.
     */
    private void initializeGame() {
        player1X = player1Y = 0;
        player2X = player2Y = 0;
        player3X = player3Y = 0;
        player4X = player4Y = 0;
        moveCount = 0;
    }

    /**
     * Moves players randomly and tracks their encounters.
     */
    private void movePlayers() {
        player1X = Math.min(Math.max(player1X + (random.nextBoolean() ? 1 : -1), 0), GRID_SIZE - 1);
        player1Y = Math.min(Math.max(player1Y + (random.nextBoolean() ? 1 : -1), 0), GRID_SIZE - 1);

        player2X = Math.min(Math.max(player2X + (random.nextBoolean() ? 1 : -1), 0), GRID_SIZE - 1);
        player2Y = Math.min(Math.max(player2Y + (random.nextBoolean() ? 1 : -1), 0), GRID_SIZE - 1);

        player3X = Math.min(Math.max(player3X + (random.nextBoolean() ? 1 : -1), 0), GRID_SIZE - 1);
        player3Y = Math.min(Math.max(player3Y + (random.nextBoolean() ? 1 : -1), 0), GRID_SIZE - 1);

        player4X = Math.min(Math.max(player4X + (random.nextBoolean() ? 1 : -1), 0), GRID_SIZE - 1);
        player4Y = Math.min(Math.max(player4Y + (random.nextBoolean() ? 1 : -1), 0), GRID_SIZE - 1);

        moveCount++;

        if ((player1X == player2X && player1Y == player2Y) ||
                (player1X == player3X && player1Y == player3Y) ||
                (player1X == player4X && player1Y == player4Y) ||
                (player2X == player3X && player2Y == player3Y) ||
                (player2X == player4X && player2Y == player4Y) ||
                (player3X == player4X && player3Y == player4Y)) {

            moveHistory.add(moveCount);

            shortestMove = Math.min(shortestMove, moveCount);
            longestMove = Math.max(longestMove, moveCount);

            int sum = moveHistory.stream().mapToInt(Integer::intValue).sum();
            int averageMove = sum / moveHistory.size();

            playerStats.clear();
            playerStats.add("Player 1: " + moveCount + " moves");
            playerStats.add("Player 2: " + moveCount + " moves");
            playerStats.add("Player 3: " + moveCount + " moves");
            playerStats.add("Player 4: " + moveCount + " moves");

            List<String> moveHistoryAsString = moveHistory.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            ResultsPage resultsPage = new ResultsPage(gradeLevel, moveHistoryAsString,
                    List.of("Longest: " + longestMove, "Shortest: " + shortestMove, "Average: " + averageMove),
                    playerStats);
            resultsPage.start(new Stage()); // Open the results page
        }
    }
}