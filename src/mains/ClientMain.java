package mains;

import javafx.application.Application;
import javafx.stage.Stage;
import users.Client;


public class ClientMain extends Application {
    // TODO: 7/7/2020 Add forget password
    // TODO: 7/7/2020 Optimize threads
    // TODO: 7/7/2020 Change updateOnline and updateFriends
    // TODO: 7/7/2020 search users by username
    // TODO: 7/7/2020 Put conditions on profile shower
    // TODO: 7/7/2020 Ai
    // TODO: 7/7/2020 Fix multiplayer
    // TODO: 7/7/2020 Add view profile button in game scene
    // TODO: 7/7/2020 can't have duplicate friends


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Client client = new Client();
    }
}
