package users;

import Serlizables.*;
import games.ServerGame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server extends User {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private final HashMap<ClientProfile, Connection> connections = new HashMap<>();
    private final File file = new File("ServerSaves/clientProfiles.dat");
    private ArrayList<ClientProfile> usersInSystem = new ArrayList<>();
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private ServerGame serverGame;

    public Server() throws Exception {
        if (!file.exists()) {
            throw new Exception("Couldn't find file");
        }
        this.userInfo = new UserInfo("Server", "");
        addUserToSystem(new Serlizables.ClientProfile("Mohammad Mahdi", "Gheysari", "m", "", Serlizables.SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "gheysari.mm@gmail.com"));
        addUserToSystem(new Serlizables.ClientProfile("Reza", "Ahmadi", "r", "", Serlizables.SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "reaza@gmail.com"));

        readFile();
        for (ClientProfile clientProfile : usersInSystem) {
            System.out.println(clientProfile.getUserInfo().getUsername() + ":" + clientProfile.getUserInfo().getPassword());
        }
        makeClientsInitiallyOffline();
        setUpServerSocket();


    }

    private void addToList(ClientProfile clientProfile) {
        addUserToSystem(clientProfile);
        writeFile();

    }

    private ArrayList<ClientProfile> cloneUsersInSystem() {
        ArrayList<ClientProfile> clientProfiles = new ArrayList<ClientProfile>();

        for (ClientProfile cp : usersInSystem) {
            ClientProfile clientProfile = new ClientProfile(cp.getFirstName(), cp.getLastName(), cp.getUserInfo().getUsername(), cp.getUserInfo().getPassword(), cp.getSecurityQuestion(), cp.getAnswer(), cp.getEmail());
            clientProfile.setSinglePlayerWins(cp.getSinglePlayerWins());
            clientProfile.setSinglePlayerLosses(cp.getSinglePlayerLosses());
            clientProfile.setTotalOnlineWins(cp.getTotalOnlineWins());
            clientProfile.setTotalOnlineLosses(cp.getTotalOnlineLosses());
            clientProfile.setOnline(cp.getOnline());
            clientProfiles.add(clientProfile);
        }
        return clientProfiles;
    }

    @Override
    public void receivePacket(Packet packet) {
        System.out.println(ANSI_RED + "[SERVER]: received packet: " + packet.getPropose().toString().toUpperCase() + ANSI_RESET);
        if (packet.getContent() instanceof UserInfo && packet.getPropose().equals(Packet.PacketPropose.LOGOUT_REQUEST)) {
            UserInfo userInfo = (UserInfo) packet.getContent();
            respondToLogoutRequest(userInfo);
        }
        if (packet.getContent() instanceof UserInfo && packet.getPropose().equals(Packet.PacketPropose.LOGIN_REQUEST)) {
            UserInfo userInfo = (UserInfo) packet.getContent();
            respondToLoginRequest(userInfo);
        }
        if (packet.getContent() instanceof ClientProfile && packet.getPropose().equals(Packet.PacketPropose.SIGN_UP_REQUEST)) {
            ClientProfile clientProfile = (ClientProfile) packet.getContent();
            respondToSignUpRequest(clientProfile);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.PLAY_TOGETHER_REQUEST)) {
            respondToPlayTogetherRequest(packet);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.RESPOND_PLAY_TOGETHER)) {
            respondToPlayTogetherRespond(packet);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.PROFILE_INFO)) {
            updateOneClient((ClientProfile) packet.getContent());
        }
        if (packet.getPropose().equals(Packet.PacketPropose.UPDATE_GAME)) {
            ClientProfile foundClient = null;
            for (ClientProfile cip : usersInSystem) {
                if (packet.getSender().getUsername().toLowerCase().equals(cip.getUserInfo().getUsername().toLowerCase())) {
                    foundClient = cip;
                }

            }
            serverGame.updateGame((Square[][]) packet.getContent(), foundClient);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.CHAT)) {
            respondToChat(packet);
        }


        sendAllToAll();
        writeFile();

    }

    private void respondToChat(Packet packet) {
        ClientProfile sender = null;
        ClientProfile receiver = null;

        for (ClientProfile cip : usersInSystem) {
            if (packet.getSenderProfile().getUserInfo().getUsername().equalsIgnoreCase(cip.getUserInfo().getUsername())) {
                sender = cip;
            }
            if (packet.getReceiverProfile().getUserInfo().getUsername().equalsIgnoreCase(cip.getUserInfo().getUsername())) {
                receiver = cip;
            }
        }

        Massage massage = (Massage) packet.getContent();

        getMassage(massage, sender, receiver);
        getMassage(massage, receiver, sender);

    }

    private void getMassage(Massage massage, ClientProfile target, ClientProfile otherOne) {

        Chat foundChat = null;
        for (Chat chat : target.getChats()) {
            for (ClientProfile cp : chat.getMembers()) {
                if (cp.getUserInfo().equals(target.getUserInfo())) {
                    foundChat = chat;
                }
            }
        }

        if (foundChat == null) {
            foundChat = new Chat(target, otherOne);
            target.getChats().add(foundChat);
        }
        int size = foundChat.getMassages().size();
        foundChat.getMassages().add(massage);
        for (Chat chat : target.getChats()) {
            for (ClientProfile cp : chat.getMembers()) {
                if (cp.getUserInfo().equals(target.getUserInfo())) {
                    int index = target.getChats().indexOf(chat);
                    target.getChats().set(index, foundChat);
                }
            }
        }
        sendOneToOne(target);

    }

    private void updateOneClient(ClientProfile clientProfile) {
        System.out.println("A client update");
        ServerMassages serverMassage = ServerMassages.UNKNOWN_ERROR;
        ClientProfile foundClient = null;
        for (ClientProfile cip : usersInSystem) {
            if (clientProfile.getUserInfo().getUsername().toLowerCase().equals(cip.getUserInfo().getUsername().toLowerCase())) {
                cip = clientProfile;
            }
        }
    }

    private void sendAllToAll() {
        ArrayList<ClientProfile> clientsInfo = new ArrayList<>();
        clientsInfo = cloneUsersInSystem();
        for (ClientProfile clientProfile : clientsInfo) {
            clientProfile.clearSecurityInfo();
        }
        for (Map.Entry<ClientProfile, Connection> connectionHashMap : connections.entrySet()) {
            connectionHashMap.getValue().sendPacket(new Packet(clientsInfo, this, Packet.PacketPropose.PROFILES_IN_SYSTEM));
        }
    }

    private void sendOneToOne(ClientProfile clientProfile) {
        Connection foundConnection = connections.get(clientProfile);
        if (clientProfile.getOnline() && foundConnection != null) {
            foundConnection.sendPacket(new Packet(clientProfile, this, Packet.PacketPropose.PROFILE_INFO));
        }

    }

    public void respondToLoginRequest(UserInfo userInfo) {
        System.out.println("A login request received");
        ServerMassages serverMassage = ServerMassages.UNKNOWN_ERROR;
        ClientProfile foundClient = null;
        for (ClientProfile cip : usersInSystem) {
            if (userInfo.getUsername().toLowerCase().equals(cip.getUserInfo().getUsername().toLowerCase())) {
                foundClient = cip;
            }
        }
        if (foundClient == null) {
            serverMassage = ServerMassages.USERNAME_NOT_FOUND;
        } else {
            if (userInfo.getPassword().equals(foundClient.getUserInfo().getPassword())) {
                usersInSystem.get(usersInSystem.indexOf(foundClient)).setOnline(true);
                serverMassage = ServerMassages.LOGIN_SUCCESSFUL;
                connections.put(foundClient, connection);
                sendOneToOne(foundClient);
            } else {
                serverMassage = ServerMassages.WRONG_PASSWORD;
                System.out.println("getPassword() was: " + foundClient.getUserInfo().getPassword() + "but client entered: " + userInfo.getPassword());
            }
        }
        connection.sendPacket(new Packet(serverMassage, this, Packet.PacketPropose.SERVER_RESPOND_TO_LOGIN));
    }

    public void respondToSignUpRequest(ClientProfile clientProfile) {
        System.out.println("A sign up request received");
        ServerMassages serverMassage = ServerMassages.UNKNOWN_ERROR;
        ClientProfile foundClient = null;
        for (ClientProfile cip : usersInSystem) {
            if (clientProfile.getUserInfo().getUsername().toLowerCase().equals(cip.getUserInfo().getUsername().toLowerCase())) {
                foundClient = cip;
            }
        }
        if (foundClient != null) {
            serverMassage = ServerMassages.USERNAME_ALREADY_EXISTS;
        } else {
            clientProfile.setOnline(true);
            addUserToSystem(clientProfile);
            serverMassage = ServerMassages.SIGN_UP_SUCCESSFUL;
            connections.put(foundClient, connection);

            sendOneToOne(clientProfile);
        }
        connection.sendPacket(new Packet(serverMassage, this, Packet.PacketPropose.SERVER_RESPOND_TO_SIGNUP));
    }

    public void respondToLogoutRequest(UserInfo userInfo) {
        System.out.println("A logout request received");

        ServerMassages serverMassage = ServerMassages.UNKNOWN_ERROR;

        ClientProfile foundClient = null;

        for (ClientProfile cip : usersInSystem) {
            if (userInfo.getUsername().toLowerCase().equals(cip.getUserInfo().getUsername().toLowerCase())) {
                foundClient = cip;
            }
        }


        if (foundClient == null) {
            serverMassage = ServerMassages.USERNAME_NOT_FOUND;
        } else {
            usersInSystem.get(usersInSystem.indexOf(foundClient)).setOnline(false);
            serverMassage = ServerMassages.LOGOUT_SUCCESSFUL;

        }
        System.out.println(usersInSystem.get(usersInSystem.indexOf(foundClient)).getUserInfo().getUsername() + " loged out.");
    }

    private void respondToPlayTogetherRespond(Packet packet) {
        ClientProfile playerX = null;
        ClientProfile playerO = null;
        ClientProfile[] clientProfiles = new ClientProfile[2];
        System.out.println("A play together respond received");
        for (Map.Entry<ClientProfile, Connection> connectionHashMap : connections.entrySet()) {
            if (connectionHashMap.getKey().getUserInfo().getUsername().toLowerCase().equals(packet.getReceiver().getUsername().toLowerCase())) {
                playerO = connectionHashMap.getKey();
            }
            if (connectionHashMap.getKey().getUserInfo().getUsername().toLowerCase().equals(packet.getSender().getUsername().toLowerCase())) {
                playerX = connectionHashMap.getKey();
            }
        }
        clientProfiles[0] = playerX;
        clientProfiles[1] = playerO;

        if (((Boolean) packet.getContent()) == true) {
            serverGame = new ServerGame(this, playerX, playerO, connections.get(playerX), connections.get(playerO));
            connections.get(playerX).sendPacket(new Packet(clientProfiles, this, Packet.PacketPropose.START_GAME));
            connections.get(playerO).sendPacket(new Packet(clientProfiles, this, Packet.PacketPropose.START_GAME));
        }
    }

    private void respondToPlayTogetherRequest(Packet packet) {
        System.out.println("A play together request received");
        for (Map.Entry<ClientProfile, Connection> connectionHashMap : connections.entrySet()) {
            if (connectionHashMap.getKey().getUserInfo().getUsername().toLowerCase().equals(packet.getReceiver().getUsername().toLowerCase())) {
                System.out.println("Found target of play request. sending packet ...");
                connectionHashMap.getValue().sendPacket(packet);
                return;
            }
        }
    }


    public void writeFile() {
        try ( // Create an output stream for file clientProfiles.dat
              ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file, false))) {
            // Write arrays to the object output stream
            output.writeObject(usersInSystem);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile() {
        try ( // Create an input stream for file clientProfiles.dat
              ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            usersInSystem = (ArrayList<ClientProfile>) (input.readObject());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
        connection.close();


    }

    private void setUpServerSocket() {


        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("users.Server is set up");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExecutorService pool = Executors.newFixedThreadPool(4);

        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            connection = new Connection(socket, this);
            pool.execute(new Thread(connection, socket.toString()));
        }
    }

    private void makeClientsInitiallyOffline() {
        for (ClientProfile clientProfile : usersInSystem) {
            clientProfile.setOnline(false);
        }
        writeFile();
    }

    private boolean addUserToSystem(ClientProfile clientProfile) {
        for (ClientProfile cp : usersInSystem) {
            if (clientProfile.getUserInfo().getUsername().equalsIgnoreCase(cp.getUserInfo().getUsername())) {
                return false;
            }
        }
        usersInSystem.add(clientProfile);
        sendAllToAll();
        writeFile();
        return true;
    }


}
