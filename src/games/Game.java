package games;

import Serlizables.Square;

public abstract class Game {


    final static int WIDTH = 600;
    final static int HEIGHT = 600;
    protected GameModes gameMode;
    protected Square[][] squares = new Square[3][3];
    private Player currentPlayer;

    public Game() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[i][j] = new Square(i, j);

            }
        }
        currentPlayer = Player.PLAYER_X;
    }

    abstract void doAboutResult(Player result);

    protected Player checkResult() {
        if (squares[0][0].getState() != Player.NONE && squares[0][0].getState() == squares[0][1].getState() && squares[0][0].getState() == squares[0][2].getState()) {
            currentPlayer = Player.NONE;
            return squares[0][0].getState();
        } else if (squares[1][0].getState() != Player.NONE && squares[1][0].getState() == squares[1][1].getState() && squares[1][0].getState() == squares[1][2].getState()) {
            currentPlayer = Player.NONE;
            return squares[1][0].getState();
        } else if (squares[2][0].getState() != Player.NONE && squares[2][0].getState() == squares[2][1].getState() && squares[2][0].getState() == squares[2][2].getState()) {
            currentPlayer = Player.NONE;
            return squares[2][0].getState();
        } else if (squares[0][0].getState() != Player.NONE && squares[0][0].getState() == squares[1][0].getState() && squares[0][0].getState() == squares[2][0].getState()) {
            currentPlayer = Player.NONE;
            return squares[0][0].getState();
        } else if (squares[0][1].getState() != Player.NONE && squares[0][1].getState() == squares[1][1].getState() && squares[0][1].getState() == squares[2][1].getState()) {
            currentPlayer = Player.NONE;
            return squares[0][1].getState();
        } else if (squares[0][2].getState() != Player.NONE && squares[0][2].getState() == squares[1][2].getState() && squares[0][2].getState() == squares[2][2].getState()) {
            currentPlayer = Player.NONE;
            return squares[0][2].getState();
        } else if (squares[0][0].getState() != Player.NONE && squares[0][0].getState() == squares[1][1].getState() && squares[0][0].getState() == squares[2][2].getState()) {
            currentPlayer = Player.NONE;
            return squares[0][0].getState();
        } else if (squares[0][2].getState() != Player.NONE && squares[0][2].getState() == squares[1][1].getState() && squares[0][2].getState() == squares[2][0].getState()) {
            currentPlayer = Player.NONE;
            return squares[0][2].getState();
        }

        boolean draw = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (squares[i][j].getState() == Player.NONE) {
                    draw = false;
                }
            }
        }

        if (draw == true) {
            currentPlayer = Player.NONE;
            return Player.NONE;
        }
        return null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public enum GameModes {
        SINGLE_PLAYER, TWO_PLAYERS_ONE_CLIENT, ONLINE
    }

    public enum Player {
        NONE, PLAYER_X, PLAYER_O
    }
}
