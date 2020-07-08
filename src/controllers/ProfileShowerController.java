package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileShowerController extends StandardController implements Initializable {
    public static Button playTogetherStatic;
    public static Button closeStatic;
    public static Button startChatStatic;
    public static Button addFriendStatic;
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
        closeStatic = close;
        playTogetherStatic = playTogether;
        startChatStatic = startChat;
        addFriendStatic = addFriend;
    }
}
