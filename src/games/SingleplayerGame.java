package games;

import Serlizables.Square;
import controllers.AppUser;
import gui.elements.SquareSkin;
import javafx.scene.input.MouseEvent;

import java.util.Random;

public class SingleplayerGame extends GameWithUI {

    Random random = new Random();


    public SingleplayerGame(AppUser appUser) {
        super(appUser);
    }

    @Override
    void doAboutResult(Player result) {
        super.doAboutResult(result);
        if (result == Player.PLAYER_X) {
            appUser.client.getClientProfile().setSinglePlayerWins(appUser.client.getClientProfile().getSinglePlayerWins() + 1);
            appUser.client.sendProfileToServer();
        } else if (result != Player.PLAYER_O) {
            appUser.client.getClientProfile().setSinglePlayerLosses(appUser.client.getClientProfile().getSinglePlayerLosses() + 1);
            appUser.client.sendProfileToServer();
        }
    }

    @Override
    public void makeUI() throws Exception {
        super.makeUI();
        setPlayersInfo(appUser.client.getClientProfile().getFirstName(), "Computer AI", "@" + appUser.client.userInfo.getUsername().toLowerCase(), "");
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
            Square square = squares[random.nextInt(3)][random.nextInt(3)];
            if (square.getState() == Player.NONE) {
                square.setState(Player.PLAYER_O);
                Player result = checkResult();
                if (result == null) {
                    setCurrentPlayer(Player.PLAYER_X);
                } else {
                    doAboutResult(result);

                }

            }
        }

    }
}
