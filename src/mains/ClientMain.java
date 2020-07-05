package mains;

import javafx.application.Application;
import javafx.stage.Stage;
import users.Client;


public class ClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Client appUser = new Client();
    }
}
