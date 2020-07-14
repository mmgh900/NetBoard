package users;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import Serlizables.SecurityQuestions;
import Serlizables.ServerMassages;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Serlizables.Packet.PacketPropose.RESPOND_ADD_FRIEND;


public class Server extends User {
    //Colors for writing in console
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    //This map saves connections of connected clients
    private final HashMap<ClientProfile, Connection> connections = new HashMap<>();

    //This is server save file
    private final File file = new File("ServerSaves/clientProfiles.dat");

    //ُThis is the arrays that keeps all users of this application, it get filled at server set up from the save file and then get written in file when needed
    private ArrayList<ClientProfile> usersInSystem = new ArrayList<>();

    //Sockets needed to connect to clients
    private ServerSocket serverSocket = null;
    private Socket socket = null;


    /**
     * @throws Exception Constructor of server
     */
    public Server() throws Exception {
        if (!file.exists()) {
            throw new Exception("Couldn't find file");
        }

        //Adds sample clients and resets server save file (Recommended to keep commented)
        addSampleClients();

        //Fill usersInSystem array list from server save file
        readFile();

        //Prints all username and passwords of clients in the system in this format "USERNAME:PASSWORD"
        for (ClientProfile clientProfile : usersInSystem) {
            System.out.println(clientProfile.getUsername() + ":" + clientProfile.getPassword());
        }

        //Makes all clients offline
        makeClientsInitiallyOffline();
        setUpServerSocket();

    }

    /**
     * Adds sample clients and resets server save file
     */
    private void addSampleClients() {
        addUserToSystem(new ClientProfile("Mohammad Mahdi", "Gheysari", "m", "1212", SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "gheysari.mm@gmail.com"));
        addUserToSystem(new ClientProfile("Reza", "Ahmadi", "r", "", SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "reaza1@gmail.com"));
        addUserToSystem(new ClientProfile("Kambiz", "Dirbaz", "k", "", SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "reaza@gmail.com"));
    }

    /**
     * assign server socket and accepts clients request in a infinite while loop
     */
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

    /**
     * makes clients initially offline :?
     */
    private void makeClientsInitiallyOffline() {
        for (ClientProfile clientProfile : usersInSystem) {
            clientProfile.setOnline(false);
        }
        writeFile();
    }

    /**
     * Fill usersInSystem array list from server save file
     */
    private void readFile() {
        try ( // Create an input stream for file clientProfiles.dat
              ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            usersInSystem = (ArrayList<ClientProfile>) (input.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes usersInSystem array list to save file
     */
    private void writeFile() {
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

    /**
     * Makes a arraylist of clients, ready to be sent to all other users
     */
    private ArrayList<ClientProfile> makeOnlineClientsPack(ClientProfile clientProfile) {
        ArrayList<ClientProfile> onlineClientsPack = new ArrayList<ClientProfile>();

        for (ClientProfile cp : usersInSystem) {
            if (cp.getOnline() && !cp.equals(clientProfile)) {
                onlineClientsPack.add(cp.makeSafeClone());
            }
        }
        return onlineClientsPack;
    }

    /**
     * @param packet Packets are pieces of data that clients or the server send.
     *               This method that is overrided from extended class User, decides to what to do
     *               with packets that are received.
     */
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
            writeFile();
        } else if (packet.getContent() instanceof ClientProfile && packet.getPropose().equals(Packet.PacketPropose.SIGN_UP_REQUEST)) {
            ClientProfile clientProfile = (ClientProfile) packet.getContent();
            respondToSignUpRequest(clientProfile);
            writeFile();
        } else if (packet.getPropose().equals(Packet.PacketPropose.RESPOND_PLAY_TOGETHER)) {
            respondToPlayTogetherRespond(packet);
        } else if (packet.getPropose().equals(RESPOND_ADD_FRIEND)) {
            respondToAddFriendRespond(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.PROFILE_INFO)) {
            updateOneClient((ClientProfile) packet.getContent());
            sendAllToAll();
            writeFile();
        } else if (packet.getPropose().equals(Packet.PacketPropose.UPDATE_GAME)) {
            respondToUpdateGame(packet);
        } else if (packet.getPropose().equals(Packet.PacketPropose.MASSAGE)) {
            respondToMassage(packet);
            writeFile();
        } else if (packet.getPropose().equals(Packet.PacketPropose.RECOVER_PASSWORD_REQUEST)) {
            respondToRecoverPassword(packet);
        }

    }

    /**
     * responds to recover password request from a client
     *
     * @param packet packet with recover password request
     */
    private void respondToRecoverPassword(Packet packet) {
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

    /**
     * responds to an update of game (two dimensional array of Squares)
     *
     * @param packet packet containing game update
     */
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

    /**
     * responds to add friend respond of target client to request of sender
     *
     * @param packet packet containing boolean as answer to the request
     */
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
            assert sender != null;
            sender.getFriends().add(receiver);
            Connection foundConnection = connections.get(sender);
            if (sender.getOnline() && foundConnection != null) {
                foundConnection.sendPacket(new Packet(sender, this, RESPOND_ADD_FRIEND));
            }
            assert receiver != null;
            receiver.getFriends().add(sender);
            Connection foundConnection2 = connections.get(receiver);
            if (sender.getOnline() && foundConnection2 != null) {
                foundConnection2.sendPacket(new Packet(receiver, this, RESPOND_ADD_FRIEND));
            }


        }
    }

    /**
     * responds to play together respond of target client to request of sender
     *
     * @param packet packet containing boolean as answer to the request
     */
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

    /**
     * just finds the target of the massage (receiver) and pass the packet to it,
     * if the targeted client be currently offline, this method saves packet massages for it
     *
     * @param packet packet containing a massage
     */
    private void respondToMassage(Packet packet) {
        ClientProfile receiver = null;

        for (ClientProfile cip : usersInSystem) {
            if (packet.getReceiverProfile().equals(cip)) {
                receiver = cip;
            }
        }

        //Saves packets for client if he/she is offline
        if (connections.get(receiver) == null) {
            assert receiver != null;
            receiver.getSavedMassages().add(packet);
        } else {
            connections.get(receiver).sendPacket(packet);
        }

    }


    /**
     * after getting the client profile from the client itself
     * updates it in server information
     *
     * @param clientProfile client profile that is going to be updated
     */
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
        ClientProfile foundClient = getAndSetClientProfile(clientProfile);
        System.out.println(clientProfile.toString() + " now has " + foundClient.getTicTacToeStatistics().getSinglePlayerLosses());
    }

    /**
     * updates a client profile
     *
     * @param clientProfile client profile thats is need to be updated
     * @return pointer to wher the client is saved in server
     */
    private ClientProfile getAndSetClientProfile(ClientProfile clientProfile) {
        ClientProfile foundClient = null;

        for (ClientProfile cip : usersInSystem) {
            if (clientProfile.equals(cip)) {
                int index = usersInSystem.indexOf(cip);
                usersInSystem.set(index, clientProfile);
                foundClient = usersInSystem.get(index);
            }
        }
        return foundClient;
    }

    /**
     * after checking information correctness, add client to the system
     *
     * @param clientProfile primary profile made by entered information in the sign up list
     */
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

    /**
     * after checking information correctness, sends load package to it
     *
     * @param clientProfile profile of the client that wants sign in the system
     */
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
                usersInSystem.get(usersInSystem.indexOf(foundClient)).getSavedMassages().clear();

                sendUpdatedProfile(foundClient);
                return;

            } else {
                serverMassage = ServerMassages.WRONG_PASSWORD;
                System.out.println("getPassword() was: " + foundClient.getPassword() + "but client entered: " + clientProfile.getPassword());
            }
        }
        connection.sendPacket(new Packet(serverMassage, this, Packet.PacketPropose.SERVER_ERROR_IN_LOGIN));
    }

    /**
     * after an update in a profile sends a safe clone of it to all online users
     *
     * @param clientProfile profile that is trying to sign up
     */
    private void sendUpdatedProfile(ClientProfile clientProfile) {
        for (Map.Entry<ClientProfile, Connection> activeConnections : connections.entrySet()) {
            if (!activeConnections.getKey().equals(clientProfile)) {
                activeConnections.getValue().sendPacket(new Packet(clientProfile.makeSafeClone(), this, Packet.PacketPropose.PROFILE_INFO));
            }
        }
    }


    /**
     * make client offline in the system and removes its connection from connections map
     *
     * @param clientProfile profile that has been updated
     */
    private void respondToLogoutRequest(ClientProfile clientProfile) {

        if (clientProfile.getOnline()) {
            System.out.println("A logout request received");
            ClientProfile foundClient = getAndSetClientProfile(clientProfile);
            System.out.println(clientProfile.toString() + " now has " + foundClient.getTicTacToeStatistics().getSinglePlayerLosses());

            connections.remove(foundClient);
            foundClient.setOnline(false);
            sendUpdatedProfile(foundClient);
            System.out.println(usersInSystem.get(usersInSystem.indexOf(foundClient)).getUsername() + " loged out. and is online: " + foundClient.getOnline());
            for (ClientProfile clientProfile1 : usersInSystem) {
                System.out.println(clientProfile1.getUsername() + ":" + clientProfile1.getPassword() + "\t" + clientProfile1.getOnline());
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


    @Override
    public void close() throws IOException {
        serverSocket.close();
        connection.close();


    }


    /**
     * adds a client to array of users info and then writes the array in the save file by useins write file method
     *
     * @param clientProfile the client profile that needs to be added to system
     */
    private void addUserToSystem(ClientProfile clientProfile) {
        for (ClientProfile cp : usersInSystem) {
            if (clientProfile.getUsername().equalsIgnoreCase(cp.getUsername())) {
                return;
            }
        }
        usersInSystem.add(clientProfile);
        writeFile();
    }

    private void sendAllToAll() {
        ArrayList<ClientProfile> clientsInfo = new ArrayList<>();
        for (Map.Entry<ClientProfile, Connection> connectionHashMap : connections.entrySet()) {
            int index = usersInSystem.indexOf(connectionHashMap.getKey());
            connectionHashMap.getValue().sendPacket(new Packet(makeOnlineClientsPack(usersInSystem.get(index)), this, Packet.PacketPropose.PROFILES_IN_SYSTEM));
        }
    }

}
