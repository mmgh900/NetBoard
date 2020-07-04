package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public static Button viewProfileStatic;
    public AnchorPane rootPane;
    public Button viewProfile;
    public Button singleplayer;
    public Button multiplayer;
    public Button online;
    public Text username;
    public Text name;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewProfileStatic = viewProfile;
    }
}
