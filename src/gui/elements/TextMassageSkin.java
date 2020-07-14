package gui.elements;

import Serlizables.Massage;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import users.Client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TextMassageSkin extends MassageSkin {
    private final Button massageBox = new Button();
    private final Label date = new Label();
    private final DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm");
    private final HBox hBox = new HBox();

    public TextMassageSkin(Massage massage, Client client) {
        super(massage, client);

        massageBox.getStyleClass().remove("button");
        massageBox.getStyleClass().add("chatBox");
        if (massage.getContent().isBlank() && massage.getContent().isEmpty()) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        massageBox.setText(massage.getContent());

        date.setText(dateFormat.format(massage.getDate()));


        //Set skin settings
        this.getChildren().add(hBox);
        hBox.setSpacing(5);
        hBox.getChildren().setAll(massageBox, date);
        if (isSelf) {
            this.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            this.setStyle("-fx-background-color: #1a73e8");
        } else {
            this.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            this.setStyle("-fx-background-color: dimgrey");
        }


    }

}
