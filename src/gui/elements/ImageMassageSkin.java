package gui.elements;

import Serlizables.Massage;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ImageMassageSkin extends HBox {
    private final boolean isFromSelf;
    private final Massage massage;
    private final ImageView imageView;
    private final Label date = new Label();
    private final DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm");

    public ImageMassageSkin(boolean isFromSelf, Massage massage) {
        this.isFromSelf = isFromSelf;
        this.massage = massage;

        this.setSpacing(5);
        this.setPadding(new Insets(5, 15, 5, 15));
        this.setPrefWidth(395);
        this.setAlignment(Pos.CENTER_LEFT);

        Image image = new Image(String.valueOf((new File(massage.getContent())).toURI()));
        imageView = new ImageView(image);
        imageView.setFitWidth(250);
        imageView.setFitHeight((image.getHeight() * 250) / image.getWidth());

        date.setText(dateFormat.format(massage.getDate()));

        this.getChildren().setAll(imageView, date);

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

    public ImageView getImageView() {
        return imageView;
    }

    public Label getDate() {
        return date;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }
}
