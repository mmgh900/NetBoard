package gui.elements;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class SquareSkin extends Button {
    private int x;
    private int y;

    public SquareSkin(int x, int y) {
        this.x = x;
        this.y = y;
        this.getStyleClass().add("square");
        this.setMinSize(150, 150);
        this.setMaxSize(150, 150);
    }

    private SquareSkin() {
    }

    private SquareSkin(String s) {
        super(s);
    }

    private SquareSkin(String s, Node node) {
        super(s, node);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
