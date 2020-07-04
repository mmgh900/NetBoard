package games;

import Serlizables.Square;
import controllers.AppUser;
import javafx.scene.input.MouseEvent;

public class TwoPlayerGame extends GameWithUI {
    public TwoPlayerGame(AppUser appUser) {
        super(appUser);
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
