package users;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import Serlizables.ServerMassages;
import Serlizables.Square;
import controllers.DefaultWindow;
import controllers.GetRespondWindow;
import games.ClientGame;
import games.GameWithUI;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import static users.Server.ANSI_PURPLE;
import static users.Server.ANSI_RESET;


public class Client extends User implements Serializable {

    public GameWithUI game;
    private DefaultWindow window;
    private final Client thisClient = this;
    public Button mainMenu;
    public Text loginSceneMassage;
    private ArrayList<ClientProfile> otherPlayers;
    private ClientProfile clientProfile;
    private Socket socket;


    public Client() {

        boolean isConnectionFailed = false;
        try {
            socket = new Socket("localhost", PORT);
            //game = new games.ClientGame(appUser, games.Game.Player.PLAYER_O, connection);
            connection = new Connection(socket, this);
            new Thread(connection, "guest connection").start();

        } catch (IOException e) {
            isConnectionFailed = true;
        }
        try {
            this.window = new DefaultWindow(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isConnectionFailed) {
            window.getLoginController().badNews("There was a problem in connecting to the server. Try again later");
        }
        try {
            window.loadLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void receivePacket(Packet packet) {
        System.out.println(ANSI_PURPLE + clientProfile.toString() + ": received packet: " + packet.getPropose().toString().toUpperCase() + ANSI_RESET);
        if (packet.getPropose().equals(Packet.PacketPropose.PROFILES_IN_SYSTEM)) {
            respondToProfilesInSystem(packet);
        }
        if (packet.getContent() instanceof ServerMassages) {
            respondToServerMassages(packet);
        }
        if (packet.getContent() instanceof ClientProfile && packet.getPropose().equals(Packet.PacketPropose.PROFILE_INFO)) {
            respondToProfileInfo(packet);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.PLAY_TOGETHER_REQUEST)) {
            if (respondToRequests(packet, Packet.PacketPropose.RESPOND_PLAY_TOGETHER))
                return;
        }
        if (packet.getPropose().equals(Packet.PacketPropose.ADD_FRIEND_REQUEST)) {
            if (respondToRequests(packet, Packet.PacketPropose.RESPOND_ADD_FRIEND))
                return;
        }
        if (packet.getPropose().equals(Packet.PacketPropose.START_GAME)) {
            respondToStartGame(packet);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.UPDATE_GAME)) {
            respondToUpdateGame(packet);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.CHAT)) {
            System.out.println();
            updateProfile((ClientProfile) packet.getContent());
            if (game instanceof ClientGame) {
                ((ClientGame) game).updateChats();
            }
        }

    }

    private void respondToServerMassages(Packet packet) {
        if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_SIGNUP)) {
            respondToServerRespondToSignUp(packet);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_LOGIN)) {
            ServerMassages serverMassage = (ServerMassages) packet.getContent();
            respondToServerRespondToLogin(serverMassage);
        }
    }

    private void respondToUpdateGame(Packet packet) {
        Square[][] squares = (Square[][]) packet.getContent();
        ClientGame clientGame = (ClientGame) game;
        clientGame.updateGame(squares);
    }

    private void respondToStartGame(Packet packet) {
        ClientGame clientGame = (ClientGame) game;
        ClientProfile[] clientProfiles = (ClientProfile[]) packet.getContent();
        clientGame.startGame(clientProfiles[0], clientProfiles[1]);

    }

    private boolean respondToRequests(Packet packet, Packet.PacketPropose respondPlayTogether) {
        if (!(game instanceof ClientGame)) {
            connection.sendPacket(new Packet(false, packet.getSenderProfile(), this.getClientProfile(), respondPlayTogether));
            return true;
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new GetRespondWindow(thisClient, packet.getSenderProfile(), packet.getPropose());
                }
            });
        }
        return false;
    }

    private void respondToProfileInfo(Packet packet) {
        updateProfile((ClientProfile) packet.getContent());
        if (game instanceof ClientGame) {
            ((ClientGame) game).update();
        }
    }

    private void respondToServerRespondToLogin(ServerMassages serverMassage) {
        if (serverMassage == ServerMassages.LOGIN_SUCCESSFUL) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    window.setTitle(clientProfile.getUsername());
                }
            });
            window.getLoginController().goodNews("Login successful. Welcome " + clientProfile.getUsername().toUpperCase() + ".");
            try {
                window.loadMenuScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            window.getLoginController().badNews(serverMassage.toString().toLowerCase().replace("_", " "));
        }
    }

    private void respondToServerRespondToSignUp(Packet packet) {
        ServerMassages serverMassage = (ServerMassages) packet.getContent();

        if (serverMassage == ServerMassages.SIGN_UP_SUCCESSFUL) {
            //window.getLoginController().goodNews("Sign up successful. Welcome " + clientProfile.getUsername().toUpperCase() + ".");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    window.setTitle(clientProfile.getUsername());
                }
            });
            try {
                window.loadMenuScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            window.getLoginController().badNews(serverMassage.toString().toLowerCase().replace("_", " "));
        }
    }

    private void respondToProfilesInSystem(Packet packet) {
        if (!(packet.getContent() instanceof ArrayList)) {
            try {
                throw new Exception("INVALID INFORMATION");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        otherPlayers = (ArrayList) packet.getContent();
        otherPlayers.removeIf(cp -> cp.equals(clientProfile));
        System.out.println(Server.ANSI_GREEN + "\t" + clientProfile.toString() + ": OTHER PLAYERS UPDATED" + ANSI_RESET);

        if (game != null && game instanceof ClientGame) {
            ClientGame clientGame = (ClientGame) game;
            clientGame.updateOnlinesList();
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
            connection.close();
        }

    }


    public Socket getSocket() {
        return socket;
    }

    public DefaultWindow getWindow() {
        return window;
    }


    public Client getThisClient() {
        return thisClient;
    }

    public ClientProfile getClientProfile() {
        return clientProfile;
    }

    public ArrayList<ClientProfile> getOtherPlayers() {
        return otherPlayers;
    }

    public void sendProfileToServer() {
        connection.sendPacket(new Packet(clientProfile, thisClient, Packet.PacketPropose.PROFILE_INFO));
    }

    public void setClientProfile(ClientProfile clientProfile) {
        this.clientProfile = clientProfile;
    }

    public void logout() {
        if (socket != null) {
            connection.sendPacket(new Packet(clientProfile, clientProfile, Packet.PacketPropose.LOGOUT_REQUEST));
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateProfile(ClientProfile clientProfile) {
        this.clientProfile = clientProfile;
    }


}
