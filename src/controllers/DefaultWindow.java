package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import users.Client;

import java.io.File;
import java.io.IOException;


public class DefaultWindow extends Stage {
    public static String defaultStylesheet = "CssFiles/Light.css";
    private final Client client;

    private final DefaultWindow thisWindow;

    //Blur effect variables
    private static final ImageView background = new ImageView();
    private Scene gameScene;
    private GameController gameController;
    private Scene menuScene;
    private MenuController menuController;
    private Scene loginScene;
    private LoginController loginController;
    private static final double BLUR_AMOUNT = 70;
    private static final Effect frostEffect = new GaussianBlur(BLUR_AMOUNT);

    public Client getClient() {
        return client;
    }

    FXMLLoader fxmlLoader = new FXMLLoader();

    public DefaultWindow(Client appUser) throws IOException {
        this.client = appUser;
        this.thisWindow = this;

        applyWindowSetting();
        makeScenes();

        //Loading scene
        // TODO: 7/9/2020 Make proper loading scene
        this.setScene(new Scene(new AnchorPane()));

        this.show();
    }

    private void makeScenes() throws IOException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/Menu.fxml").toURL());
        Parent menuRoot = fxmlLoader.load();
        menuController = fxmlLoader.getController();
        menuController.rootPane.getChildren().add(newCbox());
        menuController.setClient(client);
        menuScene = new Scene(menuRoot, 1260, 700);
        menuScene.getStylesheets().add(defaultStylesheet);


        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/GameScene.fxml").toURL());
        Parent gameRoot = fxmlLoader.load();
        gameController = fxmlLoader.getController();
        gameController.setClient(client);
        gameController.rootPane.getChildren().add(newCbox());
        gameScene = new Scene(gameRoot, 1260, 700);
        gameScene.getStylesheets().add(defaultStylesheet);

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/Login.fxml").toURL());
        Parent loginRoot = fxmlLoader.load();
        loginController = fxmlLoader.getController();
        loginController.setClient(client);

        HBox hBox = (HBox) loginController.rootPane.getChildren().get(0);
        VBox cBox = newCbox();
        loginScene = new Scene(loginRoot, 1260, 700);
        loginScene.getStylesheets().add(defaultStylesheet);

        //Making blur effect
        //background.setImage(copyBackground(this));
        loginController.rootPane.getChildren().setAll(hBox, cBox);
        loginController.rootPane.setStyle("-fx-background-color: white");
        makeSmoke(thisWindow);
        background.setImage(copyBackground(thisWindow));
        background.setEffect(frostEffect);
        background.setOpacity(0);
    }

    private void applyWindowSetting() {
        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                client.logout();
            }
        });

        this.setTitle("Net Board");
        this.setX(150);
        this.setY(50);
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);

    }

    private javafx.scene.shape.Rectangle makeSmoke(Stage stage) {
        return new javafx.scene.shape.Rectangle(stage.getWidth(), stage.getHeight(), Color.WHITESMOKE.deriveColor(0, 1, 1, 0.08));
    }

    private Image copyBackground(Stage stage) {
        final int X = 100;
        final int Y = 0;
        final int W = 1310;
        final int H = 770;


        try {
            java.awt.Robot robot = new java.awt.Robot();
            java.awt.image.BufferedImage image = robot.createScreenCapture(new java.awt.Rectangle(X, Y, W, H));


            return SwingFXUtils.toFXImage(image, null);
        } catch (java.awt.AWTException e) {
            System.out.println("The robot of doom strikes!");
            e.printStackTrace();

            return null;
        }
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
        menuController.updateProfileUiGraphics();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                thisWindow.setScene(menuScene);


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

    public GameController getGameController() {
        return gameController;
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

    public void makeDraggable(final Stage stage, final Node byNode, AnchorPane layout) {
        final Delta dragDelta = new Delta();
        byNode.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
            byNode.setCursor(Cursor.MOVE);
        });
        final BooleanProperty inDrag = new SimpleBooleanProperty(false);

        byNode.setOnMouseReleased(mouseEvent -> {
            byNode.setCursor(Cursor.HAND);

            if (inDrag.get()) {
                stage.hide();

                Timeline pause = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                    background.setImage(copyBackground(stage));
                    layout.getChildren().set(0, background);
                    stage.show();
                }));
                pause.play();
            }

            inDrag.set(false);
        });
        byNode.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);

            layout.getChildren().set(0, makeSmoke(stage));

            inDrag.set(true);
        });
        byNode.setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                byNode.setCursor(Cursor.HAND);
            }
        });
        byNode.setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                byNode.setCursor(Cursor.DEFAULT);
            }
        });
    }

    /**
     * records relative x and y co-ordinates.
     */
    private static class Delta {
        double x, y;
    }


}
