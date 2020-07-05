package games;

import Serlizables.Square;
import javafx.scene.input.MouseEvent;
import users.Client;

public class TwoPlayerGame extends GameWithUI {
    public TwoPlayerGame(Client client) {
        super(client);
        gameMode = GameModes.TWO_PLAYERS_ONE_CLIENT;

    }

    public void handleClick(MouseEvent mouseEvent) {
        Square square = (Square) mouseEvent.getSource();
        if (square.getState() == Player.NONE) {
            if (getCurrentPlayer() == Player.PLAYER_X) {
                square.setState(Player.PLAYER_X);
                repaintBoard();
                setCurrentPlayer(Player.PLAYER_O);
                doAboutResult(checkResult());
            } else if (getCurrentPlayer() == Player.PLAYER_O) {
                square.setState(Player.PLAYER_O);
                repaintBoard();
                setCurrentPlayer(Player.PLAYER_X);
                doAboutResult(checkResult());
            }
        }

    }


}
