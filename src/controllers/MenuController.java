package controllers;

import games.ClientGame;
import games.SingleplayerGame;
import games.TwoPlayerGame;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends StandardController implements Initializable {
    public static Button viewProfileStatic;
    public AnchorPane rootPane;
    public Button viewProfile;
    public Button singleplayer;
    public Button multiplayer;
    public Button online;
    public Text username;
    public Text name;

    //Set the actions to change the game mode
    EventHandler handleClicksOnMenuOptions = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            if (event.getSource() == singleplayer) {
                client.game = new SingleplayerGame(client);
            } else if (event.getSource() == multiplayer) {
                client.game = new TwoPlayerGame(client);
            } else if (event.getSource() == online) {
                client.game = new ClientGame(client);
                ((ClientGame) client.game).update();
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewProfileStatic = viewProfile;
        singleplayer.setOnMouseClicked(handleClicksOnMenuOptions);
        multiplayer.setOnMouseClicked(handleClicksOnMenuOptions);
        online.setOnMouseClicked(handleClicksOnMenuOptions);
    }

    public void updateProfileUiGraphics() {
        username.setText("@" + client.getClientProfile().getUsername().toLowerCase());
        name.setText(client.getClientProfile().getFirstName());
        viewProfile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                new ProfileViewWindow(client, client.getClientProfile());
            }
        });
    }

}
