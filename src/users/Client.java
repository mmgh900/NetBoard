package users;

import Serlizables.*;
import controllers.DefaultWindow;
import games.GameWithUI;
import gui.elements.ChatTab;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static users.Server.ANSI_PURPLE;
import static users.Server.ANSI_RESET;


public class Client extends User implements Serializable {

    public String downloadPath = "resources/ClientsFiles";
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
        } /*else if (packet.getPropose().equals(Packet.PacketPropose.PLAY_TOGETHER_REQUEST)) {
            respondToRequests(packet, Packet.PacketPropose.RESPOND_PLAY_TOGETHER);
        } else if (packet.getPropose().equals(Packet.PacketPropose.ADD_FRIEND_REQUEST)) {
            respondToRequests(packet, Packet.PacketPropose.RESPOND_ADD_FRIEND);
        }*/ else if (packet.getPropose().equals(Packet.PacketPropose.START_GAME)) {
            respondToStartGame(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.UPDATE_GAME)) {
            respondToUpdateGame(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.MASSAGE)) {
            //ClientProfile updateProfile = (ClientProfile) packet.getContent();
            giveAChatTab(packet.getSenderProfile()).receiveMassage(packet);
            //window.getGameController().updateChats(updateProfile);
            //clientProfile = updateProfile;
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
        } /*else if (packet.getPropose().equals(Packet.PacketPropose.FILE)) {
            respondToFile(packet);


        }*/
        /*}, "Respond to " + packet.getPropose().toString().toLowerCase()));*/
    }

    /*private void respondToFile(Packet packet) {
        try {
            File file = new File(downloadPath + "/" + packet.getFileName());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write((byte[]) packet.getContent());
            ChatTab chatTab = giveAChat(packet.getSenderProfile());
            Massage massage = new Massage(chatTab.getChat(), packet.getSenderProfile(), new Date(), file.getPath(), Massage.MassageType.IMAGE);
            int index = clientProfile.getChats().indexOf(chatTab.getChat());
            chatTab.getChat().getMassages().add(massage);
            game.getGameController().addMassageToChat(massage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

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

/*    private void respondToRequests(Packet packet, Packet.PacketPropose propose) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                ChatTab chatTab = giveAChat(packet.getSenderProfile());

                Massage.MassageType massageType = null;
                String massageContent = null;
                if (propose.equals(Packet.PacketPropose.RESPOND_ADD_FRIEND)) {
                    massageContent = getClientProfile().getFirstName() + ", @" + packet.getSenderProfile().getUsername().toUpperCase() + " wants to be your friend";
                    massageType = Massage.MassageType.FRIEND_REQUEST;

                } else if (propose.equals(Packet.PacketPropose.RESPOND_PLAY_TOGETHER)) {
                    massageContent = getClientProfile().getFirstName() + ", @" + packet.getSenderProfile().getUsername().toUpperCase() + " wants to play";
                    massageType = Massage.MassageType.PLAY_REQUEST;
                }

                if (massageType == null) {
                    throw new NullPointerException("Massage type is null");
                }

                Massage massage = new Massage(chatTab.getChat(), packet.getSenderProfile(), new Date(), massageContent, massageType);
                chatTab.getChat().getMassages().add(massage);
                chatTab.getMassages().getChildren().add(new RequestMassageSkin(massage, thisClient));
                System.out.println("Getting request proses is done");
            }
        });

    }*/

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
