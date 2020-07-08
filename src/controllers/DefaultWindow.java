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

    private final Scene gameScene;
    private final GameController gameController;

    private final Scene menuScene;
    private final MenuController menuController;

    private final Scene loginScene;
    private final LoginController loginController;
    private static final ImageView background = new ImageView();
    private static final double BLUR_AMOUNT = 70;

    private static final Effect frostEffect = new GaussianBlur(BLUR_AMOUNT);

    FXMLLoader fxmlLoader = new FXMLLoader();

    public DefaultWindow(Client appUser) throws IOException {
        this.setX(150);
        this.setY(50);
        this.initStyle(StageStyle.DECORATED);
        this.setResizable(false);
        this.setScene(new Scene(new AnchorPane()));

        this.client = appUser;
        this.setTitle("TicTocToe");
        this.thisWindow = this;
        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                    client.logout();
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
        gameController = fxmlLoader.getController();
        gameController.rootPane.getChildren().add(newCbox());


        background.setImage(copyBackground(this));
        background.setOpacity(50);


        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(new File("resources/FXMLFiles/Login.fxml").toURL());
        Parent loginRoot = fxmlLoader.load();
        loginController = fxmlLoader.getController();
        HBox hBox = (HBox) loginController.rootPane.getChildren().get(0);
        VBox cBox = newCbox();
        loginController.rootPane.getChildren().setAll(background, hBox, cBox);
        loginController.rootPane.setStyle("-fx-background-color: white");
        makeSmoke(thisWindow);
        background.setImage(copyBackground(thisWindow));
        background.setEffect(frostEffect);
        background.setOpacity(0);
        makeDraggable(thisWindow, cBox, loginController.rootPane);
        /*thisWindow.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (thisWindow.isFocused()) {
                    thisWindow.hide();
                    background.setImage(copyBackground(thisWindow));
                    background.setEffect(frostEffect);
                    thisWindow.show();
                }
            }
        });*/


        menuScene = new Scene(menuRoot, 1260, 700);
        gameScene = new Scene(gameRoot, 1260, 700);
        loginScene = new Scene(loginRoot, 1260, 700);


        menuScene.getStylesheets().add(defaultStylesheet);
        gameScene.getStylesheets().add(defaultStylesheet);
        loginScene.getStylesheets().add(defaultStylesheet);


        this.show();


        //primaryStage.setResizable(false);
        //primaryStage.initStyle(StageStyle.UNDECORATED);

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
        gameController.setClient(client);
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
