package controllers;


import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatBoxController implements Initializable {
    public Button send;
    public TextField textField;
    public VBox massages;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
