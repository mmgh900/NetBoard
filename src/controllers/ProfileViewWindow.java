package controllers;

import Serlizables.Chat;
import Serlizables.ClientProfile;
import Serlizables.Packet;
import games.ClientGame;
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

public class ProfileViewWindow extends Stage {
    private final Client viewer;
    private final ClientProfile profile;
    private final Stage thisWindow;

    private final Button addFriend;
    private final Button startChat;
    private final Button playTogether;
    private final Button close;

    private final Text username;
    private final Text name;
    private final Text onlineWins;
    private final Text onlineLosses;
    private final Text singleWins;
    private final Text singleLosses;

    private final Scene profileScene;
    private Parent profileRoot;
    private FXMLLoader fxmlLoader;

    public ProfileViewWindow(Client viewer, ClientProfile profile) {
        this.viewer = viewer;
        this.profile = profile;
        thisWindow = this;


        try {
            profileRoot = FXMLLoader.load(new File("resources/FXMLFiles/ProfileShower.fxml").toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileScene = new Scene(profileRoot, 800, 600);
        profileScene.getStylesheets().add(DefaultWindow.defaultStylesheet);

        name = (Text) profileScene.lookup("#name");
        username = (Text) profileScene.lookup("#username");
        onlineWins = (Text) profileScene.lookup("#onlineWins");
        onlineLosses = (Text) profileScene.lookup("#onlineLosses");
        singleWins = (Text) profileScene.lookup("#singleWins");
        singleLosses = (Text) profileScene.lookup("#singleLosses");
        startChat = ProfileShowerController.startChatStatic;
        addFriend = ProfileShowerController.addFriendStatic;

        playTogether = ProfileShowerController.playTogetherStatic;
        close = ProfileShowerController.closeStatic;
        close.setStyle("-fx-background-color: red");
        if (close == null) {
            System.out.println("it's not here");
        }

        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                thisWindow.close();
            }
        });

        name.setText(profile.getFirstName() + " " + profile.getLastName());
        username.setText("@" + profile.getUserInfo().getUsername());
        onlineWins.setText(profile.getTotalOnlineWins() + "");
        singleWins.setText(profile.getSinglePlayerWins() + "");
        onlineLosses.setText(profile.getTotalOnlineLosses() + "");
        singleLosses.setText(profile.getSinglePlayerLosses() + "");
        if (viewer.getClientProfile().getUserInfo().getUsername().toLowerCase().equals(profile.getUserInfo().getUsername().toLowerCase())) {
            playTogether.setVisible(false);
            addFriend.setVisible(false);
            startChat.setVisible(false);

        }
        addFriend.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                thisWindow.close();
                viewer.connection.sendPacket(new Packet(viewer.userInfo, viewer.getClientProfile(), profile, Packet.PacketPropose.ADD_FRIEND_REQUEST));
            }
        });

        startChat.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                thisWindow.close();
                viewer.getClientProfile().getChats().add(new Chat(viewer.getClientProfile(), profile));
                if (viewer.getAppUser().game instanceof ClientGame) {
                    ((ClientGame) viewer.getAppUser().game).update();
                }
            }
        });

        playTogether.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                thisWindow.close();
                viewer.connection.sendPacket(new Packet(viewer.userInfo, viewer.getClientProfile(), profile, Packet.PacketPropose.PLAY_TOGETHER_REQUEST));
            }
        });


        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setScene(profileScene);
        this.show();
    }
}
