package controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import users.Client;

import java.io.File;
import java.io.IOException;


public class DefaultWindow extends Stage {
    public static String defaultStylesheet = "CssFiles/Light.css";

    private final Client client;
    private final DefaultWindow thisWindow;

    FXMLLoader fxmlLoader = new FXMLLoader();
    private Scene gameScene;
    private GameController gameController;
    private Scene menuScene;
    private MenuController menuController;
    private Scene loginScene;
    private LoginController loginController;

    /**
     * Constructor and initialization methods
     */
    public DefaultWindow(Client appUser) throws IOException {
        this.client = appUser;
        this.thisWindow = this;

        applyWindowSetting();
        makeScenes();

        //Loading scene
        this.setScene(new Scene(new AnchorPane()));

        this.show();
    }
    private void makeScenes() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/Menu.fxml").toURL());
        Parent menuRoot = fxmlLoader.load();
        menuController = fxmlLoader.getController();
        menuController.rootPane.getChildren().add(newCBox());
        menuController.setClient(client);
        menuScene = new Scene(menuRoot, 1260, 700);
        menuScene.getStylesheets().add(defaultStylesheet);


        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/GameScene.fxml").toURL());
        Parent gameRoot = fxmlLoader.load();
        gameController = fxmlLoader.getController();
        gameController.setClient(client);
        gameController.rootPane.getChildren().add(newCBox());
        gameScene = new Scene(gameRoot, 1260, 700);
        gameScene.getStylesheets().add(defaultStylesheet);

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/Login.fxml").toURL());
        Parent loginRoot = fxmlLoader.load();
        loginController = fxmlLoader.getController();
        loginController.setClient(client);
        loginController.rootPane.getChildren().add(newCBox());
        loginScene = new Scene(loginRoot, 1260, 700);
        loginScene.getStylesheets().add(defaultStylesheet);
    }
    private void applyWindowSetting() {
        this.setOnCloseRequest(windowEvent -> client.logout());

        this.setTitle("Net Board");
        this.setX(150);
        this.setY(50);
        this.initStyle(StageStyle.DECORATED);
        this.setResizable(false);

    }

    /**
     * Load scenes
     */
    public void loadGameScene() {

        Platform.runLater(() -> thisWindow.setScene(gameScene));

    }

    public void loadMenuScene() {
        menuController.updateProfileUiGraphics();
        Platform.runLater(() -> thisWindow.setScene(menuScene));
    }

    public void loadLoginScene() {
        Platform.runLater(() -> thisWindow.setScene(loginScene));
    }

    public GameController getGameController() {
        return gameController;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public Client getClient() {
        return client;
    }

    /**
     * @return a VBox containing stage controls for existing and minimization
     */
    public VBox newCBox() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/StageControlls.fxml").toURL());
        fxmlLoader.load();
        StageControls stageControls = fxmlLoader.getController();
        stageControls.setClient(client);

        return stageControls.CBox;
    }


}
