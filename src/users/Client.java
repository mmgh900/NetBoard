package users;

import Serlizables.*;
import controllers.DefaultWindow;
import controllers.GetRespondWindow;
import games.ClientGame;
import games.GameWithUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;


public class Client extends User implements Serializable {

    public GameWithUI game;
    private DefaultWindow window;
    private final Client thisClient = this;
    private SignUpForm signUpForm;
    private LoginForm loginForm;
    public Button mainMenu;
    public Text loginSceneMassage;
    private ArrayList<ClientProfile> otherPlayers;
    private ClientProfile clientProfile;
    private Socket socket;

    public Client() {
        try {
            this.window = new DefaultWindow(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loginSceneMassage = window.getLoginController().massage;
        loginSceneMassage.getStyleClass().add("massage");
        loginSceneMassage.setText("");

        //this.loginForm = new LoginForm();
        //this.signUpForm = new SignUpForm();
        try {
            socket = new Socket("localhost", PORT);
            //game = new games.ClientGame(appUser, games.Game.Player.PLAYER_O, connection);
            connection = new Connection(socket, this);
            new Thread(connection, "guest connection").start();

        } catch (IOException e) {
            badNews("There was a problem in connecting to the server. Try again later");
        }
        try {
            window.loadLoginScene();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void receivePacket(Packet packet) {
        System.out.println("I got a packet with this purpose: " + packet.getPropose().toString());
        Packet.PacketPropose packetPropose = packet.getPropose();
        if (packetPropose == Packet.PacketPropose.PROFILES_IN_SYSTEM) {
            respondToProfilesInSystem(packet);
        }
        if (packet.getContent() instanceof ServerMassages) {
            if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_SIGNUP)) {
                respondToServerRespondToSignUp(packet);
            }
            if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_LOGIN)) {
                ServerMassages serverMassage = (ServerMassages) packet.getContent();
                respondToServerRespondToLogin(serverMassage);
            }
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
            goodNews("Login successful. Welcome " + clientProfile.getUsername().toUpperCase() + ".");
            try {
                window.loadMenuScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            badNews(serverMassage.toString().toLowerCase().replace("_", " "));
        }
    }

    private void respondToServerRespondToSignUp(Packet packet) {
        ServerMassages serverMassage = (ServerMassages) packet.getContent();

        if (serverMassage == ServerMassages.SIGN_UP_SUCCESSFUL) {
            goodNews("Sign up successful. Welcome " + clientProfile.getUsername().toUpperCase() + ".");
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
            badNews(serverMassage.toString().toLowerCase().replace("_", " "));
        }
    }

    private void respondToProfilesInSystem(Packet packet) {
        otherPlayers = (ArrayList<ClientProfile>) packet.getContent();
        otherPlayers.removeIf(cp -> cp.getUsername().equals(clientProfile.getUsername()));
        if (game != null && game instanceof ClientGame) {
            ClientGame clientGame = (ClientGame) game;
            clientGame.update();
        }
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
            connection.close();
        }

    }

    public void badNews(String s) {
        String s1 = s.substring(0, 1).toUpperCase();
        String massageCapitalized = s1 + s.substring(1).toLowerCase();

        loginSceneMassage.setFill(Color.RED);
        loginSceneMassage.setText(massageCapitalized);

    }

    public void goodNews(String s) {
        String s1 = s.substring(0, 1).toUpperCase();
        String massageCapitalized = s1 + s.substring(1).toLowerCase();

        loginSceneMassage.setFill(Color.GREEN);
        loginSceneMassage.setText(massageCapitalized);

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

    class SignUpForm {
        public TextField firstNameField;
        public TextField lastNameField;
        public TextField usernameField;
        public TextField emailField;
        public TextField answerField;
        public ChoiceBox choiceBox;
        public PasswordField passwordField;
        public PasswordField passwordConfirmField;
        public Button signupButton;
        EventHandler signUp = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                String firstNameS = firstNameField.getText();
                String lastNameS = lastNameField.getText();
                String usernameS = usernameField.getText();
                String passwordS = passwordField.getText();
                String confirmPasswordS = passwordConfirmField.getText();
                String emailS = emailField.getText();
                String answerS = answerField.getText();
                SecurityQuestions securityQuestionS = (SecurityQuestions) choiceBox.getValue();

                if (firstNameS.isBlank() || lastNameS.isBlank() || usernameS.isBlank() || passwordS.isBlank() || confirmPasswordS.isBlank() || emailS.isBlank() || answerS.isBlank() || securityQuestionS == null) {
                    badNews("Please fill all the text fields");
                    return;
                }
                if (!passwordS.equals(confirmPasswordS)) {
                    badNews("Passwords does not match.");
                    return;
                }

                if (passwordS.length() < 8) {
                    badNews("Password must be at least 8 characters");
                    return;
                }

                if (!emailS.toLowerCase().contains("@") || !emailS.toLowerCase().contains(".")) {
                    badNews("Invalid email");
                    return;
                }

                clientProfile = new ClientProfile(firstNameS, lastNameS, usernameS, passwordS, securityQuestionS, answerS, emailS);

                connection.sendPacket(new Packet(clientProfile, thisClient, Packet.PacketPropose.SIGN_UP_REQUEST));
            }
        };

        public SignUpForm() {

            firstNameField = (TextField) window.getLoginScene().lookup("#firstname");
            lastNameField = (TextField) window.getLoginScene().lookup("#lastname");
            usernameField = (TextField) window.getLoginScene().lookup("#username");

            emailField = (TextField) window.getLoginScene().lookup("#email");
            answerField = (TextField) window.getLoginScene().lookup("#answer");
            choiceBox = (ChoiceBox) window.getLoginScene().lookup("#question");
            passwordField = (PasswordField) window.getLoginScene().lookup("#password");
            passwordConfirmField = (PasswordField) window.getLoginScene().lookup("#confirmpassword");
            signupButton = (Button) window.getLoginScene().lookup("#signupButton");
            signupButton.setOnMouseClicked(signUp);

            choiceBox.setItems(FXCollections.observableArrayList(SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND, SecurityQuestions.WHAT_WAS_THE_LAST_NAME_OF_YOUR_THIRD_GRADE_TEACHER, SecurityQuestions.WHAT_WAS_THE_NAME_OF_YOUR_SECOND_PET, SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO));
            choiceBox.setValue(SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND);
            choiceBox.setTooltip(new Tooltip("Select a security question"));


        }

    }

    class LoginForm {
        public TextField usernameField;
        public PasswordField passwordField;
        public Button signupButton;
        EventHandler login = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                clientProfile = new ClientProfile(usernameField.getText(), passwordField.getText());
                connection.sendPacket(new Packet(clientProfile, thisClient, Packet.PacketPropose.LOGIN_REQUEST));
            }
        };

        public LoginForm() {
            usernameField = (TextField) window.getLoginScene().lookup("#loginUsername");
            passwordField = (PasswordField) window.getLoginScene().lookup("#loginPassword");
            signupButton = (Button) window.getLoginScene().lookup("#loginButton");

            signupButton.setOnMouseClicked(login);
        }
    }


}
