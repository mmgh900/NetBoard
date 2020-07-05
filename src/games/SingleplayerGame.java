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
        } else if (result != Player.PLAYER_O) {
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
