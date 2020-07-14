package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class StageControls extends StandardController implements Initializable {
    public VBox CBox;
    public Button close;
    public Button minimize;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        close.setOnMouseClicked(mouseEvent -> {
            client.logout();
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });


        minimize.setOnMouseClicked(mouseEvent -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.setIconified(true);
        });
    }
}
