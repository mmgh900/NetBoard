package games;

import Serlizables.Square;
import gui.elements.SquareSkin;
import javafx.scene.input.MouseEvent;
import users.Client;

public class TwoPlayerGame extends GameWithUI {
    public TwoPlayerGame(Client client) {
        super(client);

    }

    public void handleClick(MouseEvent mouseEvent) {
        SquareSkin squareSkin = (SquareSkin) mouseEvent.getSource();
        Square square = new Square(squareSkin.getX(), squareSkin.getY());

        if (square.getState() == Player.NONE) {
            if (getCurrentPlayer().equals(Player.PLAYER_X)) {
                square.setState(Player.PLAYER_X);
                doAboutResult(checkResult());
                setCurrentPlayer(Player.PLAYER_O);
                repaintBoard();
            } else if (getCurrentPlayer() == Player.PLAYER_O) {
                square.setState(Player.PLAYER_O);
                doAboutResult(checkResult());
                setCurrentPlayer(Player.PLAYER_X);
                repaintBoard();
            }
        }

    }


}
