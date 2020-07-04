package controllers;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class StageControls implements Initializable {
    public VBox CBox;
    public Button close;
    public Button minimize;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Stage stage = (Stage) close.getScene().getWindow();
        // do what you have to do
        stage.close();

        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.close();
            }
        });

        minimize.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setIconified(true);
            }
        });
    }
}
