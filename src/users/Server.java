package users;

import Serlizables.*;

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
    //private ServerGame serverGame;

    public Server() throws Exception {
        if (!file.exists()) {
            throw new Exception("Couldn't find file");
        }
        addSampleClients();

        readFile();
        for (ClientProfile clientProfile : usersInSystem) {
            System.out.println(clientProfile.getUsername() + ":" + clientProfile.getPassword());
        }
        makeClientsInitiallyOffline();
        setUpServerSocket();

    }

    private void addSampleClients() {
        addUserToSystem(new ClientProfile("Mohammad Mahdi", "Gheysari", "m", "1212", SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "gheysari.mm@gmail.com"));
        addUserToSystem(new ClientProfile("Reza", "Ahmadi", "r", "", SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "reaza1@gmail.com"));
        addUserToSystem(new ClientProfile("Kambiz", "Dirbaz", "k", "", SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "reaza@gmail.com"));
    }

    private void addToList(ClientProfile clientProfile) {
        addUserToSystem(clientProfile);
        writeFile();

    }

    private ArrayList<ClientProfile> makeOnlineClientsPack(ClientProfile clientProfile) {
        ArrayList<ClientProfile> onlineClientsPack = new ArrayList<ClientProfile>();

        for (ClientProfile cp : usersInSystem) {
            if (cp.getOnline() && !cp.equals(clientProfile)) {
                onlineClientsPack.add(cp.makeSafeClone());
            }
        }
        return onlineClientsPack;
    }

    @Override
    public void receivePacket(Packet packet) {

        System.out.println(ANSI_RED + "[SERVER]: received packet: " + packet.getPropose().toString().toUpperCase() + ANSI_RESET);
        if (packet.getContent() instanceof ClientProfile && packet.getPropose().equals(Packet.PacketPropose.LOGOUT_REQUEST)) {
            if (!(packet.getContent() instanceof ClientProfile) || packet.getContent() == null) {
                try {
                    throw new Exception("Invalid data");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ClientProfile clientProfile = (ClientProfile) packet.getContent();
            respondToLogoutRequest(clientProfile);
            writeFile();
        } else if (packet.getContent() instanceof ClientProfile && packet.getPropose().equals(Packet.PacketPropose.LOGIN_REQUEST)) {
            ClientProfile clientProfile = (ClientProfile) packet.getContent();
            respondToLoginRequest(clientProfile);
            //writeFile();
        } else if (packet.getContent() instanceof ClientProfile && packet.getPropose().equals(Packet.PacketPropose.SIGN_UP_REQUEST)) {
            ClientProfile clientProfile = (ClientProfile) packet.getContent();
            respondToSignUpRequest(clientProfile);
            //writeFile();
        } else if (packet.getPropose().equals(Packet.PacketPropose.PLAY_TOGETHER_REQUEST)) {
            respondToPlayTogetherRequest(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.RESPOND_PLAY_TOGETHER)) {
            respondToPlayTogetherRespond(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.ADD_FRIEND_REQUEST)) {
            respondToAddFriend(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.RESPOND_ADD_FRIEND)) {
            respondToAddFriendRespond(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.PROFILE_INFO)) {
            updateOneClient((ClientProfile) packet.getContent());
            sendAllToAll();
            writeFile();
        } else if (packet.getPropose().equals(Packet.PacketPropose.UPDATE_GAME)) {
            respondToUpdateGame(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.CHAT)) {
            respondToChat(packet);
            //writeFile();
        } else if (packet.getPropose().equals(Packet.PacketPropose.RECOVER_PASSWORD_REQUEST)) {
            rospondToRecoverPassword(packet);
        }


    }

    private void rospondToRecoverPassword(Packet packet) {
        ClientProfile clientProfile = (ClientProfile) packet.getContent();
        ServerMassages serverMassage = ServerMassages.UNKNOWN_ERROR;

        ClientProfile foundClient = null;
        for (ClientProfile cip : usersInSystem) {
            if (clientProfile.getEmail().equalsIgnoreCase(cip.getEmail())) {
                foundClient = cip;
            }
        }

        if (foundClient == null) {
            serverMassage = ServerMassages.EMAIL_NOT_FOUND;
        } else if (foundClient.getOnline()) {
            serverMassage = ServerMassages.ALREADY_LOGED_IN;
        } else {
            if (clientProfile.getSecurityQuestion().equals(foundClient.getSecurityQuestion()) && clientProfile.getAnswer().equalsIgnoreCase(foundClient.getAnswer())) {

                connection.sendPacket(new Packet(foundClient.getUsername() + ":" + foundClient.getPassword(), this, Packet.PacketPropose.RECOVER_PASSWORD_REQUEST));
                return;
            } else {
                serverMassage = ServerMassages.WRONG_QUESTION_OR_ANSWER;
            }
        }
        connection.sendPacket(new Packet(serverMassage, this, Packet.PacketPropose.SERVER_RESPOND_TO_RECOVER_PASS));

    }

    private void respondToUpdateGame(Packet packet) {
        Connection foundConnection = null;
        for (Map.Entry<ClientProfile, Connection> activeConnections : connections.entrySet()) {
            if (activeConnections.getKey().equals(packet.getReceiverProfile())) {
                activeConnections.getKey().equals(packet.getReceiverProfile());
                foundConnection = activeConnections.getValue();
            }
        }
        if (foundConnection == null) {
            try {
                throw new Exception("Failed to find game update target:" + packet.getReceiverProfile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            foundConnection.sendPacket(packet);
        }

    }

    private void respondToAddFriendRespond(Packet packet) {
        ClientProfile sender = null;
        ClientProfile receiver = null;

        for (ClientProfile cip : usersInSystem) {
            if (packet.getSenderProfile().equals(cip)) {
                sender = cip;
            }
            if (packet.getReceiverProfile().equals(cip)) {
                receiver = cip;
            }
        }

        if (packet.getContent().equals(true)) {
            sender.getFriends().add(receiver);
            sendOneToOne(sender, Packet.PacketPropose.RESPOND_ADD_FRIEND);
            receiver.getFriends().add(sender);
            sendOneToOne(receiver, Packet.PacketPropose.RESPOND_ADD_FRIEND);

        }
    }

    private void respondToAddFriend(Packet packet) {
        ClientProfile sender = null;
        ClientProfile receiver = null;

        for (ClientProfile cip : usersInSystem) {
            if (packet.getSenderProfile().getUsername().equalsIgnoreCase(cip.getUsername())) {
                sender = cip;
            }
            if (packet.getReceiverProfile().getUsername().equalsIgnoreCase(cip.getUsername())) {
                receiver = cip;
            }
        }
        boolean isAlreadyFriend = false;
        for (ClientProfile clientProfile : sender.getFriends()) {
            if (clientProfile.equals(receiver)) {
                isAlreadyFriend = true;
            }
        }
        if (!isAlreadyFriend) {
            connections.get(receiver).sendPacket(packet);
        }

    }

    private void respondToChat(Packet packet) {
        if (packet.getContent() instanceof Massage) {
            Massage massage = (Massage) packet.getContent();
            ClientProfile sender = null;
            ClientProfile receiver = null;

            for (ClientProfile cip : usersInSystem) {
                if (packet.getSenderProfile().equals(cip)) {
                    sender = cip;
                }
                if (packet.getReceiverProfile().equals(cip)) {
                    receiver = cip;
                }
            }

            getMassage(massage, sender, receiver);
            getMassage(massage, receiver, sender);
        }


    }

    private void getMassage(Massage massage, ClientProfile target, ClientProfile otherOne) {

        Chat foundChat = null;
        for (Chat chat : target.getChats()) {
            if (chat.getMembers().get(0).equals(target) && chat.getMembers().get(1).equals(otherOne)) {
                foundChat = chat;
            }
        }

        if (foundChat == null) {
            foundChat = new Chat(target, otherOne);
            foundChat.setName(otherOne.getFirstName());
            target.getChats().add(foundChat);
        }

        foundChat.getMassages().add(massage);
        foundChat.setLastMassage(massage.getDate());
        sendOneToOne(target, Packet.PacketPropose.CHAT);

    }

    private void updateOneClient(ClientProfile clientProfile) {
        System.out.println("A client update");
        if (!connections.containsKey(clientProfile)) {
            try {
                throw new Exception("Failed to find update target");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        ClientProfile foundClient = null;

        for (ClientProfile cip : usersInSystem) {
            if (clientProfile.equals(cip)) {
                int index = usersInSystem.indexOf(cip);
                usersInSystem.set(index, clientProfile);
                foundClient = usersInSystem.get(index);
            }
        }
        System.out.println(clientProfile.toString() + " now has " + foundClient.getTicTacToeStatistics().getSinglePlayerLosses());
    }


    private void sendOneToOne(ClientProfile clientProfile, Packet.PacketPropose customPurpose) {
        Connection foundConnection = connections.get(clientProfile);
        if (clientProfile.getOnline() && foundConnection != null) {
            foundConnection.sendPacket(new Packet(clientProfile, this, customPurpose));
        }

    }

    private void respondToLoginRequest(ClientProfile clientProfile) {
        System.out.println("A login request received");
        ServerMassages serverMassage;
        ClientProfile foundClient = null;
        for (ClientProfile cip : usersInSystem) {
            if (clientProfile.equals(cip)) {
                foundClient = cip;
            }
        }
        if (foundClient == null) {
            serverMassage = ServerMassages.USERNAME_NOT_FOUND;
        } else if (foundClient.getOnline()) {
            serverMassage = ServerMassages.ALREADY_LOGED_IN;
        } else {
            if (clientProfile.getPassword().equals(foundClient.getPassword())) {
                usersInSystem.get(usersInSystem.indexOf(foundClient)).setOnline(true);
                connections.put(foundClient, connection);
                connection.sendPacket(new Packet(foundClient, makeOnlineClientsPack(foundClient), this, Packet.PacketPropose.LOAD_PACKET));

                sendUpdatedProfile(foundClient);
                return;

            } else {
                serverMassage = ServerMassages.WRONG_PASSWORD;
                System.out.println("getPassword() was: " + foundClient.getPassword() + "but client entered: " + clientProfile.getPassword());
            }
        }
        connection.sendPacket(new Packet(serverMassage, this, Packet.PacketPropose.SERVER_ERROR_IN_LOGIN));
    }

    private void sendUpdatedProfile(ClientProfile clientProfile) {
        for (Map.Entry<ClientProfile, Connection> activeConnections : connections.entrySet()) {
            if (!activeConnections.getKey().equals(clientProfile)) {
                activeConnections.getValue().sendPacket(new Packet(clientProfile.makeSafeClone(), this, Packet.PacketPropose.PROFILE_INFO));
            }
        }
    }

    private void respondToSignUpRequest(ClientProfile clientProfile) {
        System.out.println("A sign up request received");
        ServerMassages serverMassage;
        ClientProfile DupplicateByUserName = null;
        ClientProfile DupplicateByEmail = null;
        for (ClientProfile cip : usersInSystem) {
            if (clientProfile.equals(cip)) {
                DupplicateByUserName = cip;
            }
            if (clientProfile.getEmail().equalsIgnoreCase(cip.getEmail())) {
                DupplicateByEmail = cip;
            }
        }
        if (DupplicateByUserName != null) {
            serverMassage = ServerMassages.USERNAME_ALREADY_EXISTS;
        } else if (DupplicateByEmail != null) {
            serverMassage = ServerMassages.EMAIL_ALREADY_EXISTS;
        } else {
            addUserToSystem(clientProfile);
            respondToLoginRequest(clientProfile);
            return;

        }
        connection.sendPacket(new Packet(serverMassage, this, Packet.PacketPropose.SERVER_RESPOND_TO_SIGNUP));
    }

    private void respondToLogoutRequest(ClientProfile clientProfile) {

        if (clientProfile.getOnline()) {
            System.out.println("A logout request received");
            ClientProfile foundClient = null;

            for (ClientProfile cip : usersInSystem) {
                if (clientProfile.equals(cip)) {
                    int index = usersInSystem.indexOf(cip);
                    usersInSystem.set(index, clientProfile);
                    foundClient = usersInSystem.get(index);
                }
            }
            System.out.println(clientProfile.toString() + " now has " + foundClient.getTicTacToeStatistics().getSinglePlayerLosses());

            if (foundClient == null) {
                try {
                    throw new Exception("Client did not found");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                connections.remove(foundClient);
                foundClient.setOnline(false);
                sendUpdatedProfile(foundClient);
                System.out.println(usersInSystem.get(usersInSystem.indexOf(foundClient)).getUsername() + " loged out. and is online: " + foundClient.getOnline());
                for (ClientProfile clientProfile1 : usersInSystem) {
                    System.out.println(clientProfile1.getUsername() + ":" + clientProfile1.getPassword() + "\t" + clientProfile1.getOnline());
                }

            }
            System.out.println(clientProfile.toString() + " now has " + foundClient.getTicTacToeStatistics().getSinglePlayerLosses());

        } else {
            try {
                throw new Exception("Client is already offline");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void respondToPlayTogetherRespond(Packet packet) {
        ClientProfile playerX = null;
        ClientProfile playerO = null;
        ClientProfile[] clientProfiles = new ClientProfile[2];
        System.out.println("A play together respond received");
        for (Map.Entry<ClientProfile, Connection> connectionHashMap : connections.entrySet()) {
            if (connectionHashMap.getKey().equals(packet.getReceiverProfile())) {
                int index = usersInSystem.indexOf(connectionHashMap.getKey());
                playerO = usersInSystem.get(index);
            }
            if (connectionHashMap.getKey().equals(packet.getSenderProfile())) {
                int index = usersInSystem.indexOf(connectionHashMap.getKey());
                playerX = usersInSystem.get(index);
            }
        }
        clientProfiles[0] = playerX;
        clientProfiles[1] = playerO;

        if (((Boolean) packet.getContent())) {
            playerO.setPlayingOnline(true);
            playerX.setPlayingOnline(true);
            connections.get(playerX).sendPacket(new Packet(clientProfiles, this, Packet.PacketPropose.START_GAME));
        }
    }

    private void respondToPlayTogetherRequest(Packet packet) {
        System.out.println("A play together request received");

        Connection foundConnection = connections.get(packet.getReceiverProfile());
        if (packet.getReceiverProfile().getOnline() && foundConnection != null) {
            System.out.println("Found target of play request. sending packet ...");
            foundConnection.sendPacket(packet);
            return;
        } else {
            try {
                throw new Exception("Error in finding play request target");
            } catch (Exception e) {
                e.printStackTrace();
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
        } catch (IOException | ClassNotFoundException e) {
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
            if (clientProfile.getUsername().equalsIgnoreCase(cp.getUsername())) {
                return false;
            }
        }
        usersInSystem.add(clientProfile);
        writeFile();
        return true;
    }

    private void sendAllToAll() {
        ArrayList<ClientProfile> clientsInfo = new ArrayList<>();
        for (Map.Entry<ClientProfile, Connection> connectionHashMap : connections.entrySet()) {
            int index = usersInSystem.indexOf(connectionHashMap.getKey());
            connectionHashMap.getValue().sendPacket(new Packet(makeOnlineClientsPack(usersInSystem.get(index)), this, Packet.PacketPropose.PROFILES_IN_SYSTEM));
        }
    }

}
