package gui.elements;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import users.Client;

public class GetRespondChatBox extends VBox {
    private final Client receiver;
    private final ClientProfile sender;
    private final Packet.PacketPropose outputPurpose;
    private final FXMLLoader fxmlLoader = new FXMLLoader();
    private final Button decline;
    private final Button accept;
    private final Text massage;
    private final ChatTab chatTab;
    private final GetRespondChatBox thisGetRespondChatBox;
    private Parent root;

    public GetRespondChatBox(Client receiver, ClientProfile senderProfile, Packet.PacketPropose outputPurpose, ChatTab chatTab) {
        thisGetRespondChatBox = this;
        this.receiver = receiver;
        this.sender = senderProfile;
        this.chatTab = chatTab;
        this.outputPurpose = outputPurpose;
        this.setStyle("-fx-background-color: blueviolet");

        this.setSpacing(5);
        this.setPadding(new Insets(5, 15, 5, 15));

        this.setAlignment(Pos.CENTER_LEFT);


        massage = new Text();
        accept = new Button("Accept");
        decline = new Button("Decline");
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();
        hBox1.getChildren().add(massage);
        hBox2.getChildren().addAll(accept, decline);
        hBox2.setSpacing(20);
        hBox1.setAlignment(Pos.CENTER);
        hBox2.setAlignment(Pos.CENTER);
        getChildren().addAll(hBox1, hBox2);
        setMassage();

        massage.getStyleClass().add("request");
        accept.getStyleClass().add("request");
        decline.getStyleClass().add("request");


        decline.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        chatTab.getMassages().getChildren().remove(thisGetRespondChatBox);
                    }
                });
                respondToChoice(false);
            }
        });

        accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        chatTab.getMassages().getChildren().remove(thisGetRespondChatBox);
                    }
                });
                respondToChoice(true);
            }
        });


    }

    public void setMassage() {
        if (outputPurpose.equals(Packet.PacketPropose.RESPOND_ADD_FRIEND)) {
            massage.setText(receiver.getClientProfile().getFirstName() + ", @" + sender.getUsername().toUpperCase() + " wants to be your friend");

        } else if (outputPurpose.equals(Packet.PacketPropose.RESPOND_PLAY_TOGETHER)) {
            massage.setText(receiver.getClientProfile().getFirstName() + ", @" + sender.getUsername().toUpperCase() + " wants to play");
        }
        this.setPrefWidth(395);
        this.setMaxWidth(395);
    }

    public void respondToChoice(boolean answer) {
        receiver.connection.sendPacket(new Packet(answer, sender, receiver.getClientProfile(), outputPurpose));
        if (outputPurpose.equals(Packet.PacketPropose.RESPOND_PLAY_TOGETHER)) {
            receiver.game.startGame(sender, receiver.getClientProfile());
        }
    }

}
