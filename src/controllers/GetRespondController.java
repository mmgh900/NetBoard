package controllers;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import users.Client;

import java.net.URL;
import java.util.ResourceBundle;

public class GetRespondController extends StandardController implements Initializable {

    public Button decline;
    public Button accept;
    public Text massage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        decline.setStyle("-fx-background-color: red");
    }

    public void setToFriendRequest(ClientProfile sender, Client receiver) {
        massage.setText(receiver.getClientProfile().getFirstName() + ", you have received a add friend request from @" + sender.getUsername().toUpperCase());
        decline.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiver.connection.sendPacket(new Packet(false, sender, receiver.getClientProfile(), Packet.PacketPropose.RESPOND_ADD_FRIEND));
                ((Stage) decline.getScene().getWindow()).close();
            }
        });

        accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiver.connection.sendPacket(new Packet(true, sender, receiver.getClientProfile(), Packet.PacketPropose.RESPOND_ADD_FRIEND));
                ((Stage) decline.getScene().getWindow()).close();
            }
        });
    }

    public void setToPlayRequest(ClientProfile sender, Client receiver) {
        massage.setText(receiver.getClientProfile().getFirstName() + ", you have received a play request from @" + sender.getUsername().toUpperCase());
        decline.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiver.connection.sendPacket(new Packet(false, sender, receiver.getClientProfile(), Packet.PacketPropose.RESPOND_PLAY_TOGETHER));
                ((Stage) decline.getScene().getWindow()).close();
            }
        });

        accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiver.connection.sendPacket(new Packet(true, sender, receiver.getClientProfile(), Packet.PacketPropose.RESPOND_PLAY_TOGETHER));
                ((Stage) decline.getScene().getWindow()).close();
            }
        });
    }
}
