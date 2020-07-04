package controllers;

import games.ClientGame;
import games.GameWithUI;
import games.SingleplayerGame;
import games.TwoPlayerGame;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import users.Client;

import java.io.IOException;

public class AppUser {

    private final AppUser thisUser;
    public DefaultWindow window;
    public GameWithUI game;
    public Client client;
    public Button singleplayer;
    public Button multiplayer;
    public Button online;
    public Text username;
    public Text name;
    public Button viewProfile;

    public Text menuMassage;

    public AppUser() {
        this.thisUser = this;

        //Make a window, set the menu scene on it
        try {
            this.window = new DefaultWindow(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Find menu buttons in menu scene
        singleplayer = (Button) window.getMenuScene().lookup("#singleplayer");
        multiplayer = (Button) window.getMenuScene().lookup("#multiplayer");
        online = (Button) window.getMenuScene().lookup("#online");
        username = (Text) window.getMenuScene().lookup("#username");
        name = (Text) window.getMenuScene().lookup("#name");
        viewProfile = MenuController.viewProfileStatic;

        //Set the actions to change the game mode
        EventHandler handleClicksOnMenuOptions = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if (event.getSource() == singleplayer) {
                    game = new SingleplayerGame(thisUser);
                } else if (event.getSource() == multiplayer) {
                    game = new TwoPlayerGame(thisUser);
                } else if (event.getSource() == online) {
                    game = new ClientGame(thisUser, thisUser.client.connection);
                    ((ClientGame) game).update();

                }
            }
        };
        singleplayer.setOnMouseClicked(handleClicksOnMenuOptions);
        multiplayer.setOnMouseClicked(handleClicksOnMenuOptions);
        online.setOnMouseClicked(handleClicksOnMenuOptions);

        goOnline();


    }

    public void updateProfileUiGraphics(Client client) {
        username.setText("@" + client.userInfo.getUsername().toLowerCase());
        name.setText(client.getClientProfile().getFirstName());
        viewProfile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                new ProfileViewWindow(client, client.getClientProfile());
            }
        });
    }

    public void updateProfileUiGraphics() {
        updateProfileUiGraphics(client);
    }

    public void goOnline() {
        //Go to login page
        if (client == null || !client.getClientProfile().getOnline()) {
            client = new Client(this);
        }
    }

}
