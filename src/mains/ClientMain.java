package mains;

import javafx.application.Application;
import javafx.stage.Stage;
import users.Client;


public class ClientMain extends Application {
    // TODO: 7/7/2020 Make threads synchronized
    // TODO: 7/7/2020 Change updateOnline and updateFriends
    // TODO: 7/7/2020 search users by username


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Client client = new Client();
    }
}
