package controllers;

import Serlizables.Chat;
import Serlizables.ClientProfile;
import Serlizables.Packet;
import gui.elements.ChatTab;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private final Label name;
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

        name = (Label) profileScene.lookup("#name");
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
        username.setText("@" + profile.getUsername());
        onlineWins.setText(profile.getTicTacToeStatistics().getTotalOnlineWins() + "");
        singleWins.setText(profile.getTicTacToeStatistics().getSinglePlayerWins() + "");
        onlineLosses.setText(profile.getTicTacToeStatistics().getTotalOnlineLosses() + "");
        singleLosses.setText(profile.getTicTacToeStatistics().getSinglePlayerLosses() + "");

        boolean isAlreadyFriend = false;
        for (ClientProfile clientProfile : viewer.getClientProfile().getFriends()) {
            if (clientProfile.equals(profile)) {
                isAlreadyFriend = true;
            }
        }
        boolean isPlayingOnline = viewer.getClientProfile().isPlayingOnline();
        boolean isAlreadyChatting = false;
        for (Chat chat : viewer.getClientProfile().getChats()) {
            if (chat.getMembers().get(1).equals(profile)) {
                isAlreadyChatting = true;
            }
        }


        startChat.setVisible(!isAlreadyChatting);
        addFriend.setVisible(!isAlreadyFriend);
        playTogether.setVisible(!isPlayingOnline && profile.getOnline());
        if (viewer.getClientProfile().equals(profile)) {
            playTogether.setVisible(false);
            addFriend.setVisible(false);
            startChat.setVisible(false);

        }

        addFriend.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                thisWindow.close();
                viewer.connection.sendPacket(new Packet(viewer.getClientProfile(), viewer.getClientProfile(), profile, Packet.PacketPropose.ADD_FRIEND_REQUEST));

                addChatWhenSendingRequest(viewer, profile);
            }
        });

        startChat.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                thisWindow.close();
                Chat newChat = new Chat(viewer.getClientProfile(), profile);
                viewer.getClientProfile().getChats().add(newChat);
                viewer.getWindow().getGameController().addChatTab(newChat);
                viewer.sendProfileToServer();
                // TODO: 7/9/2020 Rewrite update chat
                //viewer.game.getGameController().updateChats(viewer.getClientProfile());
            }
        });

        playTogether.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                thisWindow.close();
                viewer.connection.sendPacket(new Packet(Packet.PacketPropose.PLAY_TOGETHER_REQUEST, viewer.getClientProfile().makeSafeClone(), profile));

                addChatWhenSendingRequest(viewer, profile);
            }
        });


        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setScene(profileScene);
        this.show();
    }

    private void addChatWhenSendingRequest(Client viewer, ClientProfile profile) {
        Chat foundChat = null;
        ChatTab foundChatTab = null;
        for (Chat chat : viewer.getClientProfile().getChats()) {
            if (profile.equals(chat.getMembers().get(1))) {
                int index = viewer.getClientProfile().getChats().indexOf(chat);
                foundChat = viewer.getClientProfile().getChats().get(index);
                foundChatTab = (ChatTab) viewer.game.getGameController().chats.getTabs().get(index);
            }
        }
        if (foundChat == null) {
            foundChat = new Chat(viewer.getClientProfile(), profile);
            viewer.getClientProfile().getChats().add(foundChat);
            foundChatTab = new ChatTab(foundChat, viewer);
            viewer.game.getGameController().chats.getTabs().add(foundChatTab);
        }
    }

    public ProfileViewWindow(Client viewer, ClientProfile profile, Boolean isInGameScene) {
        this(viewer, profile);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playTogether.setVisible(false);
            }
        });
    }
}
