package controllers;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController extends StandardController implements Initializable {


    public AnchorPane rootPane;
    public GridPane board;
    public TabPane contacts;
    public TabPane chats;
    public Text massage;
    public Text playerXname;
    public Text playerOname;
    public Text playerXusername;
    public Text playerOusername;
    public Circle playerXsign;
    public Circle playerOsign;
    public Button playerXViewProfile;
    public Button playerOViewProfile;
    public Button mainMenuButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        massage.setText("");

        mainMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    client.getWindow().loadMenuScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
