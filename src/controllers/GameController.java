package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController extends StandardController implements Initializable {

    static public GridPane boardStatic;
    static public TabPane contactsStatic;
    static public TabPane chatsStatic;
    public static Text massageStatic;
    public static Button mainMenuButtonStatic;
    public static Text playerXnameStatic;
    public static Text playerOnameStatic;
    public static Text playerXusernameStatic;
    public static Text playerOusernameStatic;
    public static Text playerXwinsStatic;
    public static Text playerOwinsStatic;
    public static Circle playerXsignStatic;
    public static Circle playerOsignStatic;
    public AnchorPane rootPane;
    public GridPane board;
    public TabPane contacts;
    public TabPane chats;
    public Text massage;
    public Text playerXname;
    public Text playerOname;
    public Text playerXusername;
    public Text playerOusername;
    public Text playerXwins;
    public Text playerOwins;
    public Circle playerXsign;
    public Circle playerOsign;
    public Button mainMenuButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        boardStatic = board;
        contactsStatic = contacts;
        massageStatic = massage;
        mainMenuButtonStatic = mainMenuButton;
        playerXnameStatic = playerXname;
        playerOnameStatic = playerOname;
        playerXusernameStatic = playerXusername;
        playerOusernameStatic = playerOusername;
        playerXwinsStatic = playerXwins;
        playerOwinsStatic = playerOwins;
        playerXsignStatic = playerXsign;
        playerOsignStatic = playerOsign;
        chatsStatic = chats;

    }
}
