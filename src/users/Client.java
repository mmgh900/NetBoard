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
    private boolean isClosed;
    private int loadingCount = 0;


    public Client() {

        isClosed = false;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(ANSI_PURPLE + clientProfile.toString() + ": received packet: " + packet.getPropose().toString().toUpperCase() + ANSI_RESET);
                if (packet.getPropose().equals(Packet.PacketPropose.PROFILES_IN_SYSTEM)) {
                    respondToProfilesInSystem(packet);
                } else if (packet.getContent() instanceof ServerMassages) {
                    respondToServerMassages(packet);
                } else if (packet.getContent() instanceof ClientProfile && packet.getPropose().equals(Packet.PacketPropose.PROFILE_INFO)) {
                    respondToProfileInfo(packet);
                } else if (packet.getPropose().equals(Packet.PacketPropose.PLAY_TOGETHER_REQUEST)) {
                    if (respondToRequests(packet, Packet.PacketPropose.RESPOND_PLAY_TOGETHER))
                        return;
                } else if (packet.getPropose().equals(Packet.PacketPropose.ADD_FRIEND_REQUEST)) {
                    if (respondToRequests(packet, Packet.PacketPropose.RESPOND_ADD_FRIEND))
                        return;
                } else if (packet.getPropose().equals(Packet.PacketPropose.START_GAME)) {
                    respondToStartGame(packet);
                } else if (packet.getPropose().equals(Packet.PacketPropose.UPDATE_GAME)) {
                    respondToUpdateGame(packet);
                } else if (packet.getPropose().equals(Packet.PacketPropose.CHAT)) {
                    ClientProfile updateProfile = (ClientProfile) packet.getContent();
                    game.updateChats(updateProfile);
                    clientProfile = updateProfile;
                } else if (packet.getPropose().equals(Packet.PacketPropose.RESPOND_ADD_FRIEND)) {
                    ClientProfile updateProfile = (ClientProfile) packet.getContent();
                    clientProfile = updateProfile;
                    game.updateFriendsList();
                } else if (packet.getPropose().equals(Packet.PacketPropose.RECOVER_PASSWORD_REQUEST)) {
                    window.getLoginController().goodNews("YOUR USERNAME AND PASSWORD: " + packet.getContent());
                }
            }
        }, "Respond to " + packet.getPropose().toString().toLowerCase()).start();
    }


    private void respondToServerMassages(Packet packet) {
        if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_SIGNUP)) {
            respondToServerRespondToSignUp(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_LOGIN)) {
            ServerMassages serverMassage = (ServerMassages) packet.getContent();
            respondToServerRespondToLogin(serverMassage);
        } else if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_RECOVER_PASS)) {
            ServerMassages serverMassage = (ServerMassages) packet.getContent();
            respondToServerRespondToRecoverPass(serverMassage);
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
        if (game == null) {
            game = new ClientGame(this);
            game.initializeChats();
        }
        //game.update();

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

    private void respondToServerRespondToRecoverPass(ServerMassages serverMassage) {
        if (serverMassage == ServerMassages.RECOVER_PASSWORD_SUCCESSFUL) {

            //window.getLoginController().goodNews("Login successful. Welcome " + clientProfile.getUsername().toUpperCase() + ".");
        } else {
            window.getLoginController().badNews(serverMassage.toString().toLowerCase().replace("_", " "));
        }
    }

    private void respondToServerRespondToSignUp(Packet packet) {
        ServerMassages serverMassage = (ServerMassages) packet.getContent();

        if (serverMassage == ServerMassages.SIGN_UP_SUCCESSFUL) {
            //window.getLoginController().goodNews("Sign up successful. Welcome " + clientProfile.getUsername().toUpperCase() + ".");


            window.setTitle(clientProfile.getUsername());

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

        while (game == null && !isClosed) {
        }
        game.updateOnlinesList();
    }

    @Override
    public void close() throws IOException {
        isClosed = true;
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
