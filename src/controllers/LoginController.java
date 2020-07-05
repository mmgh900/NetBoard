package controllers;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends StandardController implements Initializable {
    public AnchorPane rootPane;
    public TextField loginUsername;
    public PasswordField loginPassword;
    public Button loginButton;

    public Text massage;

    EventHandler login = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            ClientProfile clientProfile = new ClientProfile(loginUsername.getText(), loginPassword.getText());
            client.connection.sendPacket(new Packet(clientProfile, client, Packet.PacketPropose.LOGIN_REQUEST));
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnMouseClicked(login);
    }
}
