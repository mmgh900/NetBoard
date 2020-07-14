package controllers;

import Serlizables.Chat;
import Serlizables.ClientProfile;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import users.Client;

import java.io.File;
import java.io.IOException;

public class ProfileViewWindow extends Stage {

    private final Client viewer;
    private ClientProfile profile;

    private final Stage thisWindow;

    private final FXMLLoader fxmlLoader = new FXMLLoader();
    private Parent profileRoot;
    private Scene profileScene = null;
    private ProfileShowerController profileShowerController;

    //Constructor
    public ProfileViewWindow(Client viewer, ClientProfile clientProfile) {

        //Assign fields
        this.viewer = viewer;
        this.profile = clientProfile;
        thisWindow = this;

        //Initialize window
        getTheUpdatedProfile(profile);
        loadWindow(profile);
        checkButtonsVisibility(viewer, profile);
        putOnClickListeners(viewer, profile);

        //set stage settings and show it
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setScene(profileScene);
        this.show();
    }

    //Defines button functionality
    private void putOnClickListeners(Client viewer, ClientProfile profile) {
        profileShowerController.addFriend.setOnMouseClicked(mouseEvent -> {
            thisWindow.close();
            viewer.giveAChatTab(profile).sendFriendRequestMassage();
        });

        profileShowerController.startChat.setOnMouseClicked(mouseEvent -> {
            thisWindow.close();
            viewer.giveAChatTab(profile);
        });

        profileShowerController.playTogether.setOnMouseClicked(event -> {
            thisWindow.close();
            viewer.giveAChatTab(profile).sendPlayRequestMassage();
        });
    }

    //Defines which buttons should be visible
    private void checkButtonsVisibility(Client viewer, ClientProfile profile) {
        boolean isAlreadyFriend = false;
        for (ClientProfile clientProfile : viewer.getClientProfile().getFriends()) {
            if (clientProfile.equals(profile)) {
                isAlreadyFriend = true;
                break;
            }
        }
        boolean isPlayingOnline = viewer.getClientProfile().isPlayingOnline();
        boolean isAlreadyChatting = false;
        for (Chat chat : viewer.getClientProfile().getChats()) {
            if (chat.getMembers().get(1).equals(profile)) {
                isAlreadyChatting = true;
                break;
            }
        }


        profileShowerController.startChat.setVisible(!isAlreadyChatting);
        profileShowerController.addFriend.setVisible(!isAlreadyFriend);
        profileShowerController.playTogether.setVisible(!isPlayingOnline && profile.getOnline());
        if (viewer.getClientProfile().equals(profile)) {
            profileShowerController.playTogether.setVisible(false);
            profileShowerController.addFriend.setVisible(false);
            profileShowerController.startChat.setVisible(false);

        }
    }

    //Loads the window(stage) and set the scene
    private void loadWindow(ClientProfile profile) {
        try {
            fxmlLoader.setLocation(new File("resources/FXMLFiles/ProfileShower.fxml").toURL());
            profileRoot = fxmlLoader.load();
            profileShowerController = fxmlLoader.getController();
            if (profileShowerController == null) {
                throw new NullPointerException("controller is null");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        profileScene = new Scene(profileRoot, 800, 600);
        profileScene.getStylesheets().add(DefaultWindow.defaultStylesheet);


        profileShowerController.close.setStyle("-fx-background-color: red");
        if (profileShowerController.close == null) {
            System.out.println("it's not here");
        }

        profileShowerController.close.setOnMouseClicked(event -> thisWindow.close());

        profileShowerController.name.setText(profile.getFirstName() + " " + profile.getLastName());
        profileShowerController.username.setText("@" + profile.getUsername());
        profileShowerController.onlineWins.setText(profile.getTicTacToeStatistics().getTotalOnlineWins() + "");
        profileShowerController.singleWins.setText(profile.getTicTacToeStatistics().getSinglePlayerWins() + "");
        profileShowerController.onlineLosses.setText(profile.getTicTacToeStatistics().getTotalOnlineLosses() + "");
        profileShowerController.singleLosses.setText(profile.getTicTacToeStatistics().getSinglePlayerLosses() + "");
    }

    //Checks to see if a updated version of profile is available in online clients array
    private void getTheUpdatedProfile(ClientProfile profile) {
        ClientProfile foundProfile = null;
        for (ClientProfile clientProfile : viewer.getOnlineClients()) {
            if (clientProfile.equals(profile)) {
                foundProfile = clientProfile;
            }
        }
        if (foundProfile != null) {
            this.profile = foundProfile;
        }

    }


}
