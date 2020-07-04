package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public static Button viewProfileStatic;
    public Button viewProfile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewProfileStatic = viewProfile;
    }
}
