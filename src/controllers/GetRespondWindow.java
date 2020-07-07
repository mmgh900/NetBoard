package controllers;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import users.Client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class GetRespondWindow extends Stage {
    private final Client receiver;
    private final ClientProfile sender;
    private final Scene scene;
    private GetRespondController getRespondController;
    private final Packet.PacketPropose purpose;
    private final FXMLLoader fxmlLoader = new FXMLLoader();
    private Parent root;

    public GetRespondWindow(Client receiver, ClientProfile senderProfile, Packet.PacketPropose purpose) {
        this.receiver = receiver;
        this.sender = senderProfile;
        this.purpose = purpose;

        try {
            fxmlLoader.setLocation(new File("resources/FXMLFiles/GetRespond.fxml").toURL());
            root = fxmlLoader.load();
            getRespondController = fxmlLoader.getController();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scene = new Scene(root, 600, 200);
        scene.getStylesheets().add(DefaultWindow.defaultStylesheet);

        if (purpose.equals(Packet.PacketPropose.ADD_FRIEND_REQUEST)) {
            getRespondController.setToFriendRequest(sender, receiver);
        } else if (purpose.equals(Packet.PacketPropose.PLAY_TOGETHER_REQUEST)) {
            getRespondController.setToPlayRequest(sender, receiver);
        }
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setScene(scene);

        this.show();
    }

}
