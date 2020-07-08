package games;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import Serlizables.Square;
import gui.elements.SquareSkin;
import javafx.scene.input.MouseEvent;
import users.Client;

public class ClientGame extends GameWithUI {

    private Player thisPlayer;
    private ClientProfile otherPlayer;

    public ClientGame(Client client) {
        super(client);
        setPlayersInfo("", "", "", "");
        board.setDisable(true);
        setCurrentPlayer(Player.NONE);
        this.client = client;

    }

    @Override
    void doAboutResult(Player result) {
        super.doAboutResult(result);
        if (result == thisPlayer) {
            client.getClientProfile().setTotalOnlineWins(client.getClientProfile().getTotalOnlineWins() + 1);
            client.sendProfileToServer();
        } else if (result != Player.NONE && result != null) {
            client.getClientProfile().setTotalOnlineLosses(client.getClientProfile().getTotalOnlineLosses() + 1);
            client.sendProfileToServer();
        }
    }

    public void startGame(ClientProfile playerX, ClientProfile playerO) {
        setPlayersInfo(playerX.getFirstName(), playerO.getFirstName(), "@" + playerX.getUsername().toLowerCase(), "@" + playerO.getUsername().toLowerCase());
        board.setDisable(false);
        if (playerX.getUsername().toLowerCase().equals(client.getClientProfile().getUsername().toLowerCase())) {
            thisPlayer = Player.PLAYER_X;
            otherPlayer = playerO;
        } else if (playerO.getUsername().toLowerCase().equals(client.getClientProfile().getUsername().toLowerCase())) {
            thisPlayer = Player.PLAYER_O;
            otherPlayer = playerX;
        }
        setCurrentPlayer(Player.PLAYER_X);
    }

    public boolean isMyTurn() {
        return getCurrentPlayer() == thisPlayer;
    }



    public void updateGame(Square[][] squares) {
        this.squares = squares;
        setCurrentPlayer(thisPlayer);
        repaintBoard();
        doAboutResult(checkResult());
    }

    @Override
    void handleClick(MouseEvent mouseEvent) {
        SquareSkin squareSkin = (SquareSkin) mouseEvent.getSource();
        Square square = squares[squareSkin.getX()][squareSkin.getY()];

        if (isMyTurn() && square.getState().equals(Player.NONE)) {
            square.setState(getCurrentPlayer());
            client.connection.sendPacket(new Packet(squares, client, Packet.PacketPropose.UPDATE_GAME));

            if (thisPlayer == Player.PLAYER_X) {
                setCurrentPlayer(Player.PLAYER_O);
            } else if (thisPlayer == Player.PLAYER_O) {
                setCurrentPlayer(Player.PLAYER_X);
            }
            repaintBoard();
            doAboutResult(checkResult());
        } else if (!isMyTurn()) {
            massage.setText("It's not your turn.");
        }
    }

    public ClientProfile getOtherPlayer() {
        return otherPlayer;
    }
}
