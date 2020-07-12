package users;

import Serlizables.*;
import controllers.DefaultWindow;
import controllers.GetRespondChatBox;
import games.GameWithUI;
import gui.elements.ChatTab;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static users.Server.ANSI_PURPLE;
import static users.Server.ANSI_RESET;


public class Client extends User implements Serializable {

    public GameWithUI game;
    private DefaultWindow window;
    private final Client thisClient = this;
    public Button mainMenu;
    public Text loginSceneMassage;
    private final int loadingCount = 0;
    private ClientProfile clientProfile;
    private Socket socket;
    private boolean isClosed;
    private final ExecutorService pool;
    private ArrayList<ClientProfile> onlineClients;


    public Client() {

        isClosed = false;
        boolean isConnected = makeConnection();
        pool = Executors.newFixedThreadPool(5);
        try {
            this.window = new DefaultWindow(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isConnected) {
            window.getLoginController().badNews("No server");
        }
        try {
            window.loadLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
        }

        game = new GameWithUI(this);
    }

    public boolean makeConnection() {
        try {
            socket = new Socket("localhost", PORT);
            connection = new Connection(socket, this);
            new Thread(connection, "guest connection").start();

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public void receivePacket(Packet packet) {
        /*pool.execute(new Thread(() -> {*/
        System.out.println(ANSI_PURPLE + clientProfile.toString() + ": received packet: " + packet.getPropose().toString().toUpperCase() + ANSI_RESET);
        if (packet.getContent() instanceof ServerMassages) {
            respondToServerMassages(packet);
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
            window.getGameController().updateChats(updateProfile);
            clientProfile = updateProfile;
        } else if (packet.getPropose().equals(Packet.PacketPropose.RESPOND_ADD_FRIEND)) {
            ClientProfile updateProfile = (ClientProfile) packet.getContent();
            clientProfile = updateProfile;
            window.getGameController().updateFriendsList();
        } else if (packet.getPropose().equals(Packet.PacketPropose.RECOVER_PASSWORD_REQUEST)) {
            window.getLoginController().goodNews("YOUR USERNAME AND PASSWORD: " + packet.getContent());
        } else if (packet.getPropose().equals(Packet.PacketPropose.LOAD_PACKET)) {
            respondToLoadPacket(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.PROFILE_INFO)) {
            respondToProfileInfo(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.PROFILES_IN_SYSTEM)) {
            respondToProfilesInSystem(packet);
        }
        /*}, "Respond to " + packet.getPropose().toString().toLowerCase()));*/
    }

    private void respondToProfileInfo(Packet packet) {
        ClientProfile clientProfile1 = (ClientProfile) packet.getContent();
        if (clientProfile1.equals(clientProfile)) {
            clientProfile = clientProfile1;
            return;
        }
        game.getGameController().aClientChangedStatus(clientProfile1);
        for (ClientProfile clientProfile2 : onlineClients) {
            if (clientProfile2.equals(clientProfile1)) {
                clientProfile2 = clientProfile1;
            }
        }
        for (ClientProfile clientProfile2 : clientProfile.getFriends()) {
            if (clientProfile2.equals(clientProfile1)) {
                clientProfile2 = clientProfile1;
            }
        }
    }

    private void respondToLoadPacket(Packet packet) {
        onlineClients = (ArrayList<ClientProfile>) packet.getContents()[1];
        clientProfile = (ClientProfile) packet.getContents()[0];

        game.load();
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
    }


    private void respondToServerMassages(Packet packet) {
        ServerMassages serverMassage = (ServerMassages) packet.getContent();
        window.getLoginController().badNews(serverMassage.toString().toLowerCase().replace("_", " "));
    }

    private void respondToUpdateGame(Packet packet) {
        Square[][] squares = (Square[][]) packet.getContent();
        game.updateGame(squares);
    }

    private void respondToStartGame(Packet packet) {
        ClientProfile[] clientProfiles = (ClientProfile[]) packet.getContent();
        game.startGame(clientProfiles[0], clientProfiles[1]);

    }

    private void respondToProfilesInSystem(Packet packet) {
        if (!(packet.getContent() instanceof ArrayList)) {
            try {
                throw new Exception("INVALID INFORMATION");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        onlineClients = (ArrayList) packet.getContent();
        if (game.getOtherPlayer() != null && onlineClients.contains(game.getOtherPlayer())) {
            int indexOfOpponent = onlineClients.indexOf(game.getOtherPlayer());
            game.setOtherPlayer(onlineClients.get(indexOfOpponent));
            if (onlineClients.size() == 1) {
                System.out.println("NOW has ; " + game.getOtherPlayer().getTicTacToeStatistics().getSinglePlayerLosses());
            }
        }
        for (ClientProfile clientProfile1 : onlineClients) {
            if (clientProfile.getFriends().contains(clientProfile1)) {
                int index = onlineClients.indexOf(clientProfile1);
                clientProfile.getFriends().set(index, onlineClients.get(index));
                game.getGameController().friends.getItems().set(index, onlineClients.get(index));
            }
            int index = onlineClients.indexOf(clientProfile1);
            onlineClients.indexOf(clientProfile1);
            game.getGameController().onlineContacts.getItems().set(index, clientProfile1);

        }

        System.out.println(Server.ANSI_GREEN + "\t" + clientProfile.toString() + ": OTHER PLAYERS UPDATED" + ANSI_RESET);
    }

    private boolean respondToRequests(Packet packet, Packet.PacketPropose propose) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //new GetRespondWindow(thisClient, packet.getSenderProfile(), packet.getPropose());
                Chat foundChat = null;
                ChatTab foundChatTab = null;
                for (Chat chat : clientProfile.getChats()) {
                    if (packet.getSenderProfile().equals(chat.getMembers().get(1))) {
                        int index = clientProfile.getChats().indexOf(foundChat);
                        foundChat = clientProfile.getChats().get(index);
                        foundChatTab = (ChatTab) game.getGameController().chats.getTabs().get(index);
                    }
                }
                if (foundChat == null) {
                    foundChat = new Chat(clientProfile, packet.getSenderProfile());
                    clientProfile.getChats().add(foundChat);
                    foundChatTab = new ChatTab(foundChat, thisClient);
                    game.getGameController().chats.getTabs().add(foundChatTab);
                }
                foundChatTab.getMassages().getChildren().add(new GetRespondChatBox(thisClient, packet.getSenderProfile(), propose, foundChatTab));
            }
        });
        return true;

    }

    @Override
    public void close() throws IOException {
        isClosed = true;
        if (socket != null) {
            socket.close();
            connection.close();
        }

    }


    public DefaultWindow getWindow() {
        return window;
    }

    public ClientProfile getClientProfile() {
        return clientProfile;
    }

    public ArrayList<ClientProfile> getOnlineClients() {
        return onlineClients;
    }

    public void sendProfileToServer() {
        System.out.println(clientProfile.toString() + " now has " + clientProfile.getTicTacToeStatistics().getSinglePlayerLosses());
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


}
