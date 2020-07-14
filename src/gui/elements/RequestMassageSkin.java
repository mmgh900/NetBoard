package gui.elements;

import Serlizables.Massage;
import Serlizables.Packet;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import users.Client;

public class RequestMassageSkin extends MassageSkin {
    private final VBox vBox = new VBox();
    private Text massageText;


    public RequestMassageSkin(Massage massage, Client client) {
        super(massage, client);
        this.massage = massage;

        //Set skin settings
        this.getChildren().add(vBox);
        vBox.setSpacing(5);
        this.setStyle("-fx-background-color: blueviolet");

        if (isSelf) {
            //Creat content for notifying successful request send
            massageText = new Text();
            massageText.getStyleClass().add("request");
            vBox.getChildren().add(massageText);
            massageText.setText(massage.getContent());
            this.setPrefWidth(395);
            this.setMaxWidth(395);
        } else {
            craetContantToGetRespond(massage);
        }
    }

    private void craetContantToGetRespond(Massage massage) {
        massageText = new Text();
        Button accept = new Button("Accept");
        Button decline = new Button("Decline");
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();
        hBox1.getChildren().add(massageText);
        hBox2.getChildren().addAll(accept, decline);
        hBox2.setSpacing(20);
        hBox1.setAlignment(Pos.CENTER);
        hBox2.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(hBox1, hBox2);
        massageText.setText(massage.getContent());


        massageText.getStyleClass().add("request");
        accept.getStyleClass().add("request");
        decline.getStyleClass().add("request");
        this.setPrefWidth(395);
        this.setMaxWidth(395);

        decline.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        massageText.setText("Request declined.");
                        getChildren().remove(hBox2);
                    }
                });
                respondToChoice(false);
            }
        });
        accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        massageText.setText("Request accepted.");
                        getChildren().remove(hBox2);
                    }
                });
                respondToChoice(true);
            }
        });
    }


    public void respondToChoice(boolean answer) {
        client.connection.sendPacket(new Packet(answer, massage.getSender(), client.getClientProfile(), Packet.PacketPropose.MASSAGE));
        if (!isSelf && massage.getMassageType().equals(Massage.MassageType.PLAY_REQUEST)) {
            client.game.startGame(massage.getSender(), client.getClientProfile());
        }
    }

}
