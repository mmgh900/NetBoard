package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileShowerController extends StandardController implements Initializable {
    public Button addFriend;
    public Button playTogether;
    public Button close;
    public Button startChat;
    public Text username;
    public Label name;
    public Text onlineWins;
    public Text onlineLosses;
    public Text singleWins;
    public Text singleLosses;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
