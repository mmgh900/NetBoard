package Serlizables;

import games.Game;

import java.io.Serializable;

public class Square implements Serializable {


/*    BackgroundFill background_fill = new BackgroundFill(Color.BLUE,
            CornerRadii.EMPTY, Insets.EMPTY);

    // create Background
    Background background = new Background(background_fill);*/


    public static final int SIZE = 150;
    private final int x;
    private final int y;
    private Game.Player state;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.state = Game.Player.NONE;


    }

    public Game.Player getState() {
        return state;
    }

    public void setState(Game.Player state) {
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
