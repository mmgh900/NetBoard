package controllers;

import Serlizables.Packet;
import Serlizables.UserInfo;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import users.Client;

import java.io.File;
import java.io.IOException;

public class GetRespondWindow extends Stage {
    private final UserInfo sender;
    private final Client receiver;
    private final Stage thisWindow;
    private final Button decline;
    private final Button accept;
    private final Text massage;
    private final Scene scene;
    private final Packet.PacketPropose purpose;
    private final FXMLLoader fxmlLoader = new FXMLLoader();
    private Parent root;

    public GetRespondWindow(UserInfo sender, Client receiver, Packet.PacketPropose purpose) {
        this.sender = sender;
        this.receiver = receiver;
        this.purpose = purpose;
        thisWindow = this;


        try {
            root = FXMLLoader.load(new File("resources/FXMLFiles/GetRespond.fxml").toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root, 600, 200);
        scene.getStylesheets().add(DefaultWindow.defaultStylesheet);


        decline = (Button) scene.lookup("#decline");
        accept = (Button) scene.lookup("#accept");
        massage = (Text) scene.lookup("#massage");

        decline.setStyle("-fx-background-color: red");

        massage.setText(receiver.getClientProfile().getFirstName() + ", you have received " + purpose.toString() + " from @" + sender.getUsername().toUpperCase() + " to play together.");

        decline.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiver.connection.sendPacket(new Packet(false, sender, receiver.getClientProfile().getUserInfo(), Packet.PacketPropose.RESPOND_PLAY_TOGETHER));
                thisWindow.close();
            }
        });

        accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                receiver.connection.sendPacket(new Packet(true, sender, receiver.getClientProfile().getUserInfo(), Packet.PacketPropose.RESPOND_PLAY_TOGETHER));
                thisWindow.close();
            }
        });

        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setScene(scene);
        this.show();
    }
}
