package gui.elements;

import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import osBased.OSDetector;
import serlizables.Massage;
import users.Client;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ImageMassageSkin extends MassageSkin {

    public ImageMassageSkin(Massage massage, Client client) {
        super(massage, client);

        //Set skin settings
        HBox hBox = new HBox();
        this.getChildren().add(hBox);
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER_LEFT);

        Label date = new Label();
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm");
        date.setText(dateFormat.format(massage.getDate()));

        if (massage.getMassageType().equals(Massage.MassageType.IMAGE)) {
            Image image = new Image(String.valueOf((new File(massage.getContent())).toURI()));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(250);
            imageView.setFitHeight((image.getHeight() * 250) / image.getWidth());
            hBox.getChildren().setAll(imageView, date);
        } else if (massage.getMassageType().equals(Massage.MassageType.FILE)) {
            Button openFile = new Button();
            openFile.setGraphic(new ImageView(new Image(String.valueOf(new File("resources/Images/file.png").toURI()))));
            openFile.setOnMouseClicked(mouseEvent -> {
                OSDetector.open(new File(massage.getContent()));
            });
            Button fileName = new Button(new File(massage.getContent()).getName());
            fileName.getStyleClass().remove("button");
            fileName.getStyleClass().add("chatBox");
            fileName.setWrapText(true);
            hBox.getChildren().setAll(openFile, fileName, date);
        }


        if (isSelf) {
            this.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            this.setStyle("-fx-background-color: #1a73e8");
        } else {
            this.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            this.setStyle("-fx-background-color: dimgrey");
        }


    }

}

