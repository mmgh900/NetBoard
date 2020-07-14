package serlizables;

import games.TicTacToe;

import java.io.Serializable;

public class Square implements Serializable {

    public static final int SIZE = 150;
    private final int x;
    private final int y;
    private TicTacToe.Player state;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.state = TicTacToe.Player.NONE;


    }

    public TicTacToe.Player getState() {
        return state;
    }

    public void setState(TicTacToe.Player state) {
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
