package controllers;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class StageControls extends StandardController implements Initializable {
    public VBox CBox;
    public Button close;
    public Button minimize;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Shape shape = new Shape() {
            @Override
            public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
                return super.getCssMetaData();
            }
        };

        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (client != null) {
                    client.logout();
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
