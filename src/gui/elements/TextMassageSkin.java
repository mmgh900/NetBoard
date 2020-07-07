package gui.elements;

import Serlizables.Massage;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TextMassageSkin extends HBox {
    private final boolean isFromSelf;
    private final Massage massage;
    private final Button massageBox = new Button();
    private final Label date = new Label();
    private final DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm");

    public TextMassageSkin(boolean isFromSelf, Massage massage) {
        this.isFromSelf = isFromSelf;
        this.massage = massage;

        this.setSpacing(5);
        this.setPadding(new Insets(5, 15, 5, 15));
        this.setPrefWidth(395);
        this.setAlignment(Pos.CENTER_LEFT);

        massageBox.getStyleClass().remove("button");
        massageBox.getStyleClass().add("chatBox");
        massageBox.setText(massage.getContent());

        date.setText(dateFormat.format(massage.getDate()));

        this.getChildren().setAll(massageBox, date);

        if (isFromSelf) {
            this.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            this.setStyle("-fx-background-color: #1a73e8");
        } else {
            this.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            this.setStyle("-fx-background-color: dimgrey");
        }


    }

    public boolean isFromSelf() {
        return isFromSelf;
    }

    public Massage getMassage() {
        return massage;
    }

    public Button getMassageBox() {
        return massageBox;
    }

    public Label getDate() {
        return date;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }
}
