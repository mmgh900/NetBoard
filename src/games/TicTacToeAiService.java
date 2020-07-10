package games;

import Serlizables.Square;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.Random;

public class TicTacToeAiService extends Service<Square> {

    private final GameWithUI game;
    private final Random random = new Random();
    private Square square;

    public TicTacToeAiService(GameWithUI game) {
        this.game = game;
        game.setCurrentPlayer(GameWithUI.Player.PLAYER_O);


        this.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {


                ((Square) workerStateEvent.getSource().getValue()).setState(GameWithUI.Player.PLAYER_O);
                game.gameController.repaintBoard(game.squares);
                GameWithUI.Player result = game.checkResult(game.squares);

                if (result == null) {
                    game.setCurrentPlayer(GameWithUI.Player.PLAYER_X);
                } else {

                    game.doForResult(result);
                }
                if (result == GameWithUI.Player.PLAYER_O) {
                    game.getGameController().playerOusername.setText("XD");
                } else {
                    game.getGameController().playerOusername.setText(":|");
                }
            }
        });
    }

    @Override
    protected Task<Square> createTask() {
        Task<Square> task = new Task<Square>() {
            @Override
            protected Square call() throws Exception {
                int thinkingTime = random.nextInt(3) + 2;

                for (int i = 0; i < thinkingTime; i++) {
                    try {
                        game.getGameController().playerOusername.setText("Thinking");
                        Thread.sleep(150);
                        game.getGameController().playerOusername.setText("Thinking.");
                        Thread.sleep(150);
                        game.getGameController().playerOusername.setText("Thinking..");
                        Thread.sleep(150);
                        game.getGameController().playerOusername.setText("Thinking...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return ai();

            }
        };
        return task;
    }

    public Square ai() {

        var bestMove = new int[]{0, 0};
        var bestScore = -100;

        Square[][] squaresClone = new Square[3][3];
        for (int o = 0; o < 3; o++) {
            for (int k = 0; k < 3; k++) {
                Square sq = new Square(o, k);
                sq.setState(game.squares[o][k].getState());
                squaresClone[o][k] = sq;
            }
        }


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Square sq = squaresClone[i][j];
                if (sq.getState().equals(GameWithUI.Player.NONE)) {
                    sq.setState(GameWithUI.Player.PLAYER_O);
                    int score = minMax(squaresClone, 0, false);
                    sq.setState(GameWithUI.Player.NONE);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        return game.squares[bestMove[0]][bestMove[1]];
    }

    private int minMax(Square[][] squares, int depth, boolean isMaximizing) {
        GameWithUI.Player result = game.checkResult(squares);
        if (result != null) {
            if (result.equals(GameWithUI.Player.PLAYER_X)) {
                return -1;
            } else if (result.equals(GameWithUI.Player.PLAYER_O)) {
                return 1;
            } else if (result.equals(GameWithUI.Player.NONE)) {
                return 0;
            }
        }

        if (isMaximizing) {
            int bestScore = -100;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Square sq = squares[i][j];
                    if (sq.getState().equals(GameWithUI.Player.NONE)) {
                        sq.setState(GameWithUI.Player.PLAYER_O);
                        int score = minMax(squares, depth + 1, false);
                        sq.setState(GameWithUI.Player.NONE);
                        bestScore = Integer.max(score, bestScore);

                    }
                }
            }

            return bestScore;
        } else {
            int worstScore = 100;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Square sq = squares[i][j];
                    if (sq.getState().equals(GameWithUI.Player.NONE)) {
                        sq.setState(GameWithUI.Player.PLAYER_X);
                        int score = minMax(squares, depth + 1, true);
                        sq.setState(GameWithUI.Player.NONE);
                        worstScore = Integer.min(score, worstScore);
                    }
                }
            }

            return worstScore;
        }
    }
}
