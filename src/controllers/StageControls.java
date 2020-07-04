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
    private AppUser appUser;

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (appUser.client != null) {
                    appUser.client.logout();
                }
                Stage stage = (Stage) close.getScene().getWindow();
                stage.close();
            }
        });


        minimize.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Stage stage = (Stage) close.getScene().getWindow();
                stage.setIconified(true);
            }
        });
    }
}
