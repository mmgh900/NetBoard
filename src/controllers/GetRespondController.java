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

    private Client receiver;
    private ClientProfile sender;
    private Packet.PacketPropose packetPropose;

    public Client getReceiver() {
        return receiver;
    }

    public void setReceiver(Client receiver) {
        this.receiver = receiver;
    }

    public ClientProfile getSender() {
        return sender;
    }

    public void setSender(ClientProfile sender) {
        this.sender = sender;
    }

    public Packet.PacketPropose getPacketPropose() {
        return packetPropose;
    }

    public void setPacketPropose(Packet.PacketPropose packetPropose) {
        this.packetPropose = packetPropose;
    }

    public Button decline;
    public Button accept;
    public Text massage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        decline.setStyle("-fx-background-color: red");
        decline.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ((Stage) decline.getScene().getWindow()).close();
                respondToChoice(false);
            }
        });

        accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ((Stage) decline.getScene().getWindow()).close();
                respondToChoice(true);
            }
        });
    }


    public void setMassage() {
        massage.setText(receiver.getClientProfile().getFirstName() + ", you have received a add friend request from @" + sender.getUsername().toUpperCase());
    }

    public void respondToChoice(boolean answer) {
        receiver.connection.sendPacket(new Packet(answer, sender, receiver.getClientProfile(), packetPropose));
        if (packetPropose.equals(Packet.PacketPropose.PLAY_TOGETHER_REQUEST)) {
            receiver.game.startGame(sender, receiver.getClientProfile());
        }
    }
}
