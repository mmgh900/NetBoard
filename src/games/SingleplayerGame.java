package games;

import Serlizables.Square;
import gui.elements.SquareSkin;
import javafx.scene.input.MouseEvent;
import users.Client;

import java.util.Random;

public class SingleplayerGame extends GameWithUI {

    Random random = new Random();


    public SingleplayerGame(Client client) {
        super(client);
    }

    @Override
    void doAboutResult(Player result) {
        super.doAboutResult(result);

        if (result == Player.PLAYER_X) {
            client.getClientProfile().setSinglePlayerWins(client.getClientProfile().getSinglePlayerWins() + 1);
            client.sendProfileToServer();
        } else if (result == Player.PLAYER_O) {
            client.getClientProfile().setSinglePlayerLosses(client.getClientProfile().getSinglePlayerLosses() + 1);
            client.sendProfileToServer();
        }
    }

    @Override
    public void makeUI() throws Exception {
        super.makeUI();
        setPlayersInfo(client.getClientProfile().getFirstName(), "Computer AI", "@" + client.getClientProfile().getUsername().toLowerCase(), "");
    }

    @Override
    void handleClick(MouseEvent mouseEvent) {
        SquareSkin squareSkin = (SquareSkin) mouseEvent.getSource();
        Square square = squares[squareSkin.getX()][squareSkin.getY()];
        if (square.getState() == Player.NONE) {
            if (getCurrentPlayer() == Player.PLAYER_X) {

                square.setState(Player.PLAYER_X);
                repaintBoard();
                Player result = checkResult();

                if (result == null) {
                    setCurrentPlayer(Player.PLAYER_O);
                    ai();
                } else {
                    doAboutResult(result);
                }


            }
        }
    }

    public void ai() {
        while (getCurrentPlayer() == Player.PLAYER_O) {
            var bestMove = new int[]{0, 0};
            var bestScore = -100;


            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Square sq = squares[i][j];
                    if (sq.getState().equals(Player.NONE)) {
                        sq.setState(Player.PLAYER_O);
                        int score = minMax(squares, 0, false);
                        sq.setState(Player.NONE);
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = new int[]{i, j};
                        }
                    }
                }
            }

            squares[bestMove[0]][bestMove[1]].setState(Player.PLAYER_O);
            Player result = checkResult();
            repaintBoard();

            if (result == null) {
                setCurrentPlayer(Player.PLAYER_X);
            } else {
                doAboutResult(result);
            }


/*            Square square = squares[random.nextInt(3)][random.nextInt(3)];
            if (square.getState() == Player.NONE) {
                square.setState(Player.PLAYER_O);
                Player result = checkResult();
                repaintBoard();

                if (result == null) {
                    setCurrentPlayer(Player.PLAYER_X);
                } else {
                    doAboutResult(result);

                }

            }*/
        }

    }

    private int minMax(Square[][] squares, int depth, boolean isMaximizing) {
        Player result = checkResult();
        if (result != null) {
            if (result.equals(Player.PLAYER_X)) {
                return -1;
            } else if (result.equals(Player.PLAYER_O)) {
                return 1;
            } else if (result.equals(Player.NONE)) {
                return 0;
            }
        }

        if (isMaximizing) {
            int bestScore = -100;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Square sq = squares[i][j];
                    if (sq.getState().equals(Player.NONE)) {
                        sq.setState(Player.PLAYER_O);
                        int score = minMax(squares, depth + 1, false);
                        sq.setState(Player.NONE);
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
                    if (sq.getState().equals(Player.NONE)) {
                        sq.setState(Player.PLAYER_X);
                        int score = minMax(squares, depth + 1, true);
                        sq.setState(Player.NONE);
                        worstScore = Integer.min(score, worstScore);
                    }
                }
            }

            return worstScore;
        }
    }
}
