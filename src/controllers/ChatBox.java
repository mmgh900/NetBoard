package controllers;

import Serlizables.ClientProfile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import users.Client;

public class ChatBox extends Application {
    private final Client viewer;
    private final String chatName;
    private final ClientProfile otherPerson;
    private ClientProfile[] otherPeople;


    public ChatBox(Client viewer, ClientProfile otherPerson) {
        this.viewer = viewer;
        this.otherPerson = otherPerson;
        this.chatName = otherPerson.getFirstName();
    }

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene;
        Parent root;
        root = FXMLLoader.load(getClass().getResource("ProfileShower.fxml"));
        scene = new Scene(root, 355, 395);
    }

}
