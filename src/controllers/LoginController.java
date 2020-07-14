package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import serlizables.ClientProfile;
import serlizables.Packet;
import serlizables.SecurityQuestions;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends StandardController implements Initializable {
    public AnchorPane rootPane;
    public TextField loginUsername;
    public PasswordField loginPassword;
    public Button loginButton;

    public TextField emailRP;
    public ChoiceBox<SecurityQuestions> questionRP;
    public TextField answerRP;
    public Button recoverPasswordButton;

    public TextField firstname;
    public TextField lastname;
    public TextField username;
    public TextField email;
    public TextField answer;
    public ChoiceBox<SecurityQuestions> question;
    public PasswordField password;
    public PasswordField confirmpassword;
    public Button signupButton;
    public Label massage;
    public Label goToLogin;
    public Label goToLoginRP;
    public Label goToSignUp;
    public Label goToSignUpRP;
    public Label goToForgetPassword;
    public VBox loginForm;
    public VBox recoverPassword;
    public ScrollPane signupForm;


    EventHandler<MouseEvent> signUp = new EventHandler<>() {
        public void handle(MouseEvent event) {
            String firstNameS = firstname.getText();
            String lastNameS = lastname.getText();
            String usernameS = username.getText();
            String passwordS = password.getText();
            String confirmPasswordS = confirmpassword.getText();
            String emailS = email.getText();
            String answerS = answer.getText();
            SecurityQuestions securityQuestionS = question.getValue();

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

            client.setClientProfile(new ClientProfile(firstNameS, lastNameS, usernameS, passwordS, securityQuestionS, answerS, emailS));

            client.connection.sendPacket(new Packet(client.getClientProfile(), client, Packet.PacketPropose.SIGN_UP_REQUEST));
        }
    };
    EventHandler<MouseEvent> login = new EventHandler<>() {
        public void handle(MouseEvent event) {
            client.setClientProfile(new ClientProfile(loginUsername.getText(), loginPassword.getText()));
            client.connection.sendPacket(new Packet(client.getClientProfile(), client, Packet.PacketPropose.LOGIN_REQUEST));
        }
    };
    EventHandler<MouseEvent> recover = new EventHandler<>() {
        public void handle(MouseEvent event) {
            client.setClientProfile(new ClientProfile(emailRP.getText(), questionRP.getValue(), answerRP.getText()));
            client.connection.sendPacket(new Packet(client.getClientProfile(), client, Packet.PacketPropose.RECOVER_PASSWORD_REQUEST));
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signupForm.setVisible(true);
        loginForm.setVisible(false);
        recoverPassword.setVisible(false);
        loadForms(goToLogin, goToSignUp);
        loadForms(goToLoginRP, goToSignUpRP);
        goToForgetPassword.setOnMouseClicked(mouseEvent -> {
            signupForm.setVisible(false);
            loginForm.setVisible(false);
            recoverPassword.setVisible(true);
        });


        loginButton.setOnMouseClicked(login);
        signupButton.setOnMouseClicked(signUp);
        recoverPasswordButton.setOnMouseClicked(recover);
        question.setItems(FXCollections.observableArrayList(SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND, SecurityQuestions.WHAT_WAS_THE_LAST_NAME_OF_YOUR_THIRD_GRADE_TEACHER, SecurityQuestions.WHAT_WAS_THE_NAME_OF_YOUR_SECOND_PET, SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO));
        question.setValue(SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND);
        question.setTooltip(new Tooltip("Select a security question"));

        questionRP.setItems(FXCollections.observableArrayList(SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND, SecurityQuestions.WHAT_WAS_THE_LAST_NAME_OF_YOUR_THIRD_GRADE_TEACHER, SecurityQuestions.WHAT_WAS_THE_NAME_OF_YOUR_SECOND_PET, SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO));
        questionRP.setValue(SecurityQuestions.WHAT_IS_THE_NAME_OF_YOUR_FAVORITE_CHILDHOOD_FRIEND);
        questionRP.setTooltip(new Tooltip("Select a security question"));
    }

    private void loadForms(Label goToLoginRP, Label goToSignUpRP) {
        goToLoginRP.setOnMouseClicked(mouseEvent -> {
            signupForm.setVisible(false);
            loginForm.setVisible(true);
            recoverPassword.setVisible(false);
        });
        goToSignUpRP.setOnMouseClicked(mouseEvent -> {
            signupForm.setVisible(true);
            loginForm.setVisible(false);
            recoverPassword.setVisible(false);
        });
    }

    public void badNews(String s) {

        Platform.runLater(() -> {
            String s1 = s.substring(0, 1).toUpperCase();
            String massageCapitalized = s1 + s.substring(1).toLowerCase();

            massage.setTextFill(Color.RED);
            massage.setText(massageCapitalized);
        });


    }
    public void goodNews(String s) {

        Platform.runLater(() -> {
            String s1 = s.substring(0, 1).toUpperCase();
            String massageCapitalized = s1 + s.substring(1).toLowerCase();

            massage.setTextFill(Color.GREEN);
            massage.setText(massageCapitalized);
        });


    }
}
