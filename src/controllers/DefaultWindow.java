package controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import users.Client;

import java.io.File;
import java.io.IOException;

public class DefaultWindow extends Stage {
    public static String defaultStylesheet = "CssFiles/Light.css";
    private final Client client;
    private final DefaultWindow thisWindow;
    private final Scene gameScene;
    private final Scene menuScene;
    private final Scene loginScene;
    private final GameSceneController gameSceneController;
    private final MenuController menuController;
    private final LoginController loginController;


    FXMLLoader fxmlLoader = new FXMLLoader();

    public DefaultWindow(Client appUser) throws IOException {

        this.client = appUser;
        this.setTitle("TicTocToe");
        this.thisWindow = this;
        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (client != null) {
                    client.logout();
                }
            }
        });


        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/Menu.fxml").toURL());
        Parent menuRoot = fxmlLoader.load();
        menuController = fxmlLoader.getController();
        menuController.rootPane.getChildren().add(newCbox());

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/GameScene.fxml").toURL());
        Parent gameRoot = fxmlLoader.load();
        gameSceneController = fxmlLoader.getController();
        gameSceneController.rootPane.getChildren().add(newCbox());

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/Login.fxml").toURL());
        Parent loginRoot = fxmlLoader.load();
        loginController = fxmlLoader.getController();
        loginController.rootPane.getChildren().add(newCbox());


        menuScene = new Scene(menuRoot, 1260, 700);
        gameScene = new Scene(gameRoot, 1260, 700);
        loginScene = new Scene(loginRoot, 1260, 700);


        menuScene.getStylesheets().add(defaultStylesheet);
        gameScene.getStylesheets().add(defaultStylesheet);
        loginScene.getStylesheets().add(defaultStylesheet);

        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.setScene(new Scene(new AnchorPane()));
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
        gameSceneController.setClient(client);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                thisWindow.setScene(gameScene);
            }
        });

    }

    public void loadMenuScene() throws IOException {
        menuController.setClient(client);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                thisWindow.setScene(menuScene);
                menuController.updateProfileUiGraphics();
            }
        });
    }

    public void loadLoginScene() throws IOException {
        loginController.setClient(client);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                thisWindow.setScene(loginScene);

            }
        });
    }

    public GameSceneController getGameSceneController() {
        return gameSceneController;
    }

    public MenuController getMenuController() {
        return menuController;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public VBox newCbox() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/Close.fxml").toURL());
        Parent controlParent = fxmlLoader.load();
        StageControls stageControls = fxmlLoader.getController();
        stageControls.setClient(client);

        VBox cBox = stageControls.CBox;
        return cBox;
    }


}
