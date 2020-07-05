package users;

import Serlizables.*;
import controllers.AppUser;
import controllers.GetRespondWindow;
import games.ClientGame;
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

    private final AppUser appUser;
    private final Client thisClient = this;
    private final SignUpForm signUpForm;
    private final LoginForm loginForm;
    public Button mainMenu;
    public Text loginSceneMassage;
    private ArrayList<ClientProfile> otherPlayers;
    private ClientProfile clientProfile;
    private Socket socket;
    EventHandler logoutHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            logout();
            connection.sendPacket(new Packet(userInfo, thisClient, Packet.PacketPropose.LOGOUT_REQUEST));
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Client(appUser);
        }
    };

    public Client(AppUser appUser) {


        this.appUser = appUser;

        loginSceneMassage = (Text) appUser.window.getLoginScene().lookup("#massage");
        loginSceneMassage.getStyleClass().add("massage");
        loginSceneMassage.setText("");

        this.loginForm = new LoginForm();
        this.signUpForm = new SignUpForm();

        try {
            socket = new Socket("localhost", PORT);
            //game = new games.ClientGame(appUser, games.Game.Player.PLAYER_O, connection);
            connection = new Connection(socket, this);
            new Thread(connection, "guest connection").start();

        } catch (IOException e) {
            badNews("There was a problem in connecting to the server. Try again later");
        }

    }

    public void logout() {
        if (socket != null) {
            connection.sendPacket(new Packet(userInfo, thisClient, Packet.PacketPropose.LOGOUT_REQUEST));
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateProfile(ClientProfile clientProfile) {
        userInfo = clientProfile.getUserInfo();
        this.clientProfile = clientProfile;
    }

    @Override
    public void receivePacket(Packet packet) {
        System.out.println("I got a packet with this purpose: " + packet.getPropose().toString());
        Packet.PacketPropose packetPropose = packet.getPropose();
        if (packetPropose == Packet.PacketPropose.PROFILES_IN_SYSTEM) {
            otherPlayers = (ArrayList<ClientProfile>) packet.getContent();
            otherPlayers.removeIf(cp -> cp.getUserInfo().getUsername().equals(userInfo.getUsername()));
            if (appUser.game != null && appUser.game instanceof ClientGame) {
                ClientGame clientGame = (ClientGame) appUser.game;
                clientGame.update();
            }
        }
        if (packet.getContent() instanceof ServerMassages) {
            if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_SIGNUP)) {
                ServerMassages serverMassage = (ServerMassages) packet.getContent();

                if (serverMassage == ServerMassages.SIGN_UP_SUCCESSFUL) {
                    goodNews("Sign up successful. Welcome " + userInfo.getUsername().toUpperCase() + ".");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            appUser.window.setTitle(userInfo.getUsername());
                        }
                    });
                    try {
                        appUser.window.loadMenuScene();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    badNews(serverMassage.toString().toLowerCase().replace("_", " "));
                }
            }
            if (packet.getPropose().equals(Packet.PacketPropose.SERVER_RESPOND_TO_LOGIN)) {
                ServerMassages serverMassage = (ServerMassages) packet.getContent();
                if (serverMassage == ServerMassages.LOGIN_SUCCESSFUL) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            appUser.window.setTitle(userInfo.getUsername());
                        }
                    });
                    goodNews("Login successful. Welcome " + userInfo.getUsername().toUpperCase() + ".");
                    try {
                        appUser.window.loadMenuScene();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    badNews(serverMassage.toString().toLowerCase().replace("_", " "));
                }
            }

        }
        if (packet.getContent() instanceof ClientProfile && packet.getPropose().equals(Packet.PacketPropose.PROFILE_INFO)) {
            updateProfile((ClientProfile) packet.getContent());
            if (appUser.game instanceof ClientGame) {
                ((ClientGame) appUser.game).update();
            }
        }
        if (packet.getPropose().equals(Packet.PacketPropose.PLAY_TOGETHER_REQUEST)) {
            if (!(appUser.game instanceof ClientGame)) {
                connection.sendPacket(new Packet(false, packet.getSender(), this.getClientProfile().getUserInfo(), Packet.PacketPropose.RESPOND_PLAY_TOGETHER));
                return;
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new GetRespondWindow(packet.getSender(), thisClient, packet.getPropose());
                    }
                });
            }
        }
        if (packet.getPropose().equals(Packet.PacketPropose.ADD_FRIEND_REQUEST)) {
            if (!(appUser.game instanceof ClientGame)) {
                connection.sendPacket(new Packet(false, packet.getSenderProfile(), this.getClientProfile(), Packet.PacketPropose.RESPOND_ADD_FRIEND));
                return;
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        new GetRespondWindow(thisClient, packet.getSenderProfile(), packet.getPropose());
                    }
                });
            }
        }
        if (packet.getPropose().equals(Packet.PacketPropose.START_GAME)) {
            ClientGame clientGame = (ClientGame) appUser.game;
            ClientProfile[] clientProfiles = (ClientProfile[]) packet.getContent();
            clientGame.startGame(clientProfiles[0], clientProfiles[1]);
        }
        if (packet.getPropose().equals(Packet.PacketPropose.UPDATE_GAME)) {
            Square[][] squares = (Square[][]) packet.getContent();
            ClientGame clientGame = (ClientGame) appUser.game;
            clientGame.updateGame(squares);
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

    public AppUser getAppUser() {
        return appUser;
    }

    public Socket getSocket() {
        return socket;
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
                userInfo = new UserInfo(usernameS, passwordS);
                ClientProfile clientProfile = new ClientProfile(firstNameS, lastNameS, usernameS, passwordS, securityQuestionS, answerS, emailS);

                connection.sendPacket(new Packet(clientProfile, thisClient, Packet.PacketPropose.SIGN_UP_REQUEST));
            }
        };

        public SignUpForm() {

            firstNameField = (TextField) appUser.window.getLoginScene().lookup("#firstname");
            lastNameField = (TextField) appUser.window.getLoginScene().lookup("#lastname");
            usernameField = (TextField) appUser.window.getLoginScene().lookup("#username");

            emailField = (TextField) appUser.window.getLoginScene().lookup("#email");
            answerField = (TextField) appUser.window.getLoginScene().lookup("#answer");
            choiceBox = (ChoiceBox) appUser.window.getLoginScene().lookup("#question");
            passwordField = (PasswordField) appUser.window.getLoginScene().lookup("#password");
            passwordConfirmField = (PasswordField) appUser.window.getLoginScene().lookup("#confirmpassword");
            signupButton = (Button) appUser.window.getLoginScene().lookup("#signupButton");
            signupButton.setOnMouseClicked(signUp);

            choiceBox.setItems(FXCollections.observableArrayList(SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND, SecurityQuestions.WHAT_WAS_THE_LAST_NAME_OF_YOUR_THIRD_GRADE_TEACHER, SecurityQuestions.WHAT_WAS_THE_NAME_OF_YOUR_SECOND_PET, SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO));
            choiceBox.setValue(SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND);
            choiceBox.setTooltip(new Tooltip("Select a security question"));




            /*choiceBox.getItems().add(Serlizables.SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND.toString());
            choiceBox.getItems().add(Serlizables.SecurityQuestions.WHAT_WAS_THE_LAST_NAME_OF_YOUR_THIRD_GRADE_TEACHER.toString());
            choiceBox.getItems().add(Serlizables.SecurityQuestions.WHAT_WAS_THE_NAME_OF_YOUR_SECOND_PET.toString());
            choiceBox.getItems().add(Serlizables.SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO.toString());*/


        }

    }

    class LoginForm {
        public TextField usernameField;
        public PasswordField passwordField;
        public Button signupButton;
        EventHandler login = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                userInfo = new UserInfo(usernameField.getText(), passwordField.getText());
                connection.sendPacket(new Packet(userInfo, thisClient, Packet.PacketPropose.LOGIN_REQUEST));
            }
        };

        public LoginForm() {
            usernameField = (TextField) appUser.window.getLoginScene().lookup("#loginUsername");
            passwordField = (PasswordField) appUser.window.getLoginScene().lookup("#loginPassword");
            signupButton = (Button) appUser.window.getLoginScene().lookup("#loginButton");

            signupButton.setOnMouseClicked(login);
        }
    }


}
