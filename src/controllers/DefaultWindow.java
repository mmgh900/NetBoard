package controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DefaultWindow extends Stage {
    public static String defaultStylesheet = "CssFiles/Light.css";
    private final AppUser appUser;
    private final DefaultWindow thisWindow;
    private final Scene gameScene;
    private final Scene menuScene;
    private final Scene loginScene;
    FXMLLoader fxmlLoader = new FXMLLoader();

    public DefaultWindow(AppUser appUser) throws IOException {

        this.appUser = appUser;
        this.setTitle("TicTocToe");
        this.thisWindow = this;
        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (appUser.client != null) {
                    appUser.client.logout();
                }
            }
        });


        URL url = new File("resources/FXMLFiles/Menu.fxml").toURL();
        Parent menuRoot = FXMLLoader.load(new File("resources/FXMLFiles/Menu.fxml").toURL());
        Parent gameRoot = FXMLLoader.load(new File("resources/FXMLFiles/GameScene.fxml").toURL());
        Parent loginRoot = FXMLLoader.load(new File("resources/FXMLFiles/Login.fxml").toURL());


        menuScene = new Scene(menuRoot, 1260, 700);
        gameScene = new Scene(gameRoot, 1260, 700);
        loginScene = new Scene(loginRoot, 1260, 700);


        menuScene.getStylesheets().add(defaultStylesheet);
        gameScene.getStylesheets().add(defaultStylesheet);
        loginScene.getStylesheets().add(defaultStylesheet);

        //this.initStyle(StageStyle.UNDECORATED);
        this.setScene(loginScene);
        this.show();


        //primaryStage.setResizable(false);
        //primaryStage.initStyle(StageStyle.UNDECORATED);

    }

    public Scene getGameScene() {
        return gameScene;
    }

    public Scene getMenuScene() {
        return menuScene;
    }

    public Scene getLoginScene() {
        return loginScene;
    }

    public void loadGameScene() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                thisWindow.setScene(gameScene);
            }
        });

    }

    public void loadMenuScene() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                thisWindow.setScene(menuScene);
                appUser.updateProfileUiGraphics();
            }
        });
    }

    public void loadLoginScene() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                thisWindow.setScene(loginScene);

            }
        });
    }


}
