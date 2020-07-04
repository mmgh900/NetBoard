package mains;

import controllers.AppUser;
import javafx.application.Application;
import javafx.stage.Stage;


public class ClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        AppUser appUser = new AppUser();
        //new controllers.ProfileViewWindow(appUser.client, games.GameWithUI.me);
    }
}
