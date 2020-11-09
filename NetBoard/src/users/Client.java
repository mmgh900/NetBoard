package users;

import controllers.DefaultWindow;
import controllers.ProfileViewWindow;
import games.TicTacToe;
import gui.elements.ChatTab;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import serlizables.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import static users.Server.ANSI_PURPLE;
import static users.Server.ANSI_RESET;


public class Client extends User implements Serializable {

    public String downloadPath = "resources/ClientsFiles";
    public TicTacToe game;
    private DefaultWindow window;
    private final Client thisClient = this;
    private ClientProfile clientProfile;
    private Socket socket;
    private ArrayList<ClientProfile> onlineClients;


    public Client() {

        boolean isConnected = makeConnection();
        try {
            this.window = new DefaultWindow(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isConnected) {
            window.getLoginController().badNews("No server");
        }
        window.loadLoginScene();

        game = new TicTacToe(this);
    }

    public boolean makeConnection() {
        try {
            socket = new Socket("localhost", PORT);
            socketStreamManager = new SocketStreamManager(socket, this);
            new Thread(socketStreamManager, "guest connection").start();

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public void receivePacket(Packet packet) {
        System.out.println(ANSI_PURPLE + clientProfile.toString() + ": received packet: " + packet.getPropose().toString().toUpperCase() + ANSI_RESET);
        if (packet.getContent() instanceof ServerMassages) {
            respondToServerMassages(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.START_GAME)) {
            respondToStartGame(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.UPDATE_GAME)) {
            respondToUpdateGame(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.MASSAGE)) {
            giveAChatTab(packet.getSenderProfile()).receiveMassage(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.RESPOND_ADD_FRIEND)) {
            clientProfile = (ClientProfile) packet.getContent();
            window.getGameController().updateFriendsList();
        } else if (packet.getPropose().equals(Packet.PacketPropose.RECOVER_PASSWORD_REQUEST)) {
            window.getLoginController().goodNews("YOUR USERNAME AND PASSWORD: " + packet.getContent());
        } else if (packet.getPropose().equals(Packet.PacketPropose.LOAD_PACKET)) {
            respondToLoadPacket(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.PROFILE_INFO)) {
            respondToProfileInfo(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.PROFILES_IN_SYSTEM)) {
            respondToProfilesInSystem(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.SEARCH_USERNAME)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new ProfileViewWindow(thisClient, (ClientProfile) packet.getContent());
                }
            });

        }
    }


    private void respondToProfileInfo(Packet packet) {
        ClientProfile clientProfile1 = (ClientProfile) packet.getContent();
        if (clientProfile1.equals(clientProfile)) {
            clientProfile = clientProfile1;
            return;
        }
        game.getGameController().aClientChangedStatus(clientProfile1);

        int index = -1;

        for (ClientProfile clientProfile2 : onlineClients) {
            if (clientProfile2.equals(clientProfile1)) {
                index = onlineClients.indexOf(clientProfile2);
            }
        }
        if (index != -1) {
            onlineClients.set(index, clientProfile1);
        }

        for (ClientProfile clientProfile2 : clientProfile.getFriends()) {
            if (clientProfile2.equals(clientProfile1)) {
                index = onlineClients.indexOf(clientProfile2);
            }
        }
        if (index != -1) {
            onlineClients.set(index, clientProfile1);
        }
    }

    private void respondToLoadPacket(Packet packet) {
        onlineClients = (ArrayList<ClientProfile>) packet.getContents()[1];
        clientProfile = (ClientProfile) packet.getContents()[0];
        downloadPath = downloadPath + "/" + clientProfile.getUsername();
        File file = new File(downloadPath);
        if (!file.exists()) {
            file.mkdir();
        }
        if (!file.exists()) {
            try {
                throw new IOException("Error in finding / making client directory");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        game.load();

        Platform.runLater(() -> window.setTitle(clientProfile.getUsername()));

        window.getLoginController().goodNews("Login successful. Welcome " + clientProfile.getUsername().toUpperCase() + ".");

        window.loadMenuScene();

        /*ArrayList<Packet> packets = new ArrayList<>();
        for (Packet packet1 : clientProfile.getSavedMassages()) {
            packet.
        }*/
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Packet packet1 : clientProfile.getSavedMassages()) {
                giveAChatTab(packet1.getSenderProfile()).receiveMassage(packet1);
            }
            clientProfile.getSavedMassages().clear();
        }).start();

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
        onlineClients = (ArrayList<ClientProfile>) packet.getContent();
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

    public ChatTab giveAChatTab(ClientProfile otherSide) {
        ChatTab foundChatTab = null;

        for (Tab tab : game.getGameController().chats.getTabs()) {
            ChatTab chatTab = (ChatTab) tab;
            if (chatTab.getChat().getMembers().get(1).equals(otherSide)) {
                foundChatTab = chatTab;
            }
        }


        if (foundChatTab == null) {
            Chat chat = new Chat(clientProfile, otherSide);
            if (clientProfile.getChats().contains(chat)) {
                try {
                    throw new Exception("Chat with this profile already existed");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            clientProfile.getChats().add(chat);
            foundChatTab = new ChatTab(chat, thisClient);
        }
        return foundChatTab;
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
            socketStreamManager.close();
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
        socketStreamManager.sendPacket(new Packet(clientProfile, thisClient, Packet.PacketPropose.PROFILE_INFO));
    }

    public void setClientProfile(ClientProfile clientProfile) {
        this.clientProfile = clientProfile;
    }

    public void logout() {
        if (socket != null) {
            socketStreamManager.sendPacket(new Packet(clientProfile, clientProfile, Packet.PacketPropose.LOGOUT_REQUEST));
            try {
                close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
