package games;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import Serlizables.Square;
import users.Connection;
import users.Server;

import java.util.HashMap;

public class ServerGame extends Game {


    private final Server server;
    private final ClientProfile playerX;
    private final ClientProfile playerO;
    private final HashMap<ClientProfile, Connection> connections = new HashMap<>();

    public ServerGame(Server server, ClientProfile playerX, ClientProfile playerO, Connection playerXConnection, Connection playerOConnection) {
        super();
        this.server = server;
        this.playerX = playerX;
        this.playerO = playerO;
        connections.put(playerX, playerXConnection);
        connections.put(playerO, playerOConnection);

    }

    @Override
    void doAboutResult(Player result) {


    }

    public void updateGame(Square[][] squares, ClientProfile sender) {
        this.squares = squares;

        if (sender.equals(playerO)) {
            connections.get(playerX).sendPacket(new Packet(squares, server, Packet.PacketPropose.UPDATE_GAME));
            setCurrentPlayer(Player.PLAYER_X);
        }
        if (sender.equals(playerX)) {
            connections.get(playerO).sendPacket(new Packet(squares, server, Packet.PacketPropose.UPDATE_GAME));
            setCurrentPlayer(Player.PLAYER_O);
        }
        doAboutResult(checkResult());
    }


}
