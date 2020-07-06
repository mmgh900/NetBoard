package games;

import Serlizables.ClientProfile;
import Serlizables.SecurityQuestions;
import controllers.GameSceneController;
import gui.elements.SquareSkin;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;

public abstract class GameWithUI extends Game {

    public static ClientProfile me = new ClientProfile("Mohammad Mahdi", "Gheysari", "mmgh900", "1234", SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "gheysari.mm@gmail.com");
    protected Scene gameScene;
    protected GridPane board;

    protected Cli appUser;
    protected Text massage;

    protected Text playerXname;
    protected Text playerOname;
    protected Text playerXusername;
    protected Text playerOusername;
    protected Text playerXwins;
    protected Text playerOwins;
    protected Circle playerXsign;
    protected Circle playerOsign;
    protected SquareSkin[][] squareSkins = new SquareSkin[3][3];

    public GameWithUI(AppUser appUser) {
        super();
        this.appUser = appUser;
        this.massage = GameSceneController.massageStatic;
        playerXname = GameSceneController.playerXnameStatic;
        playerOname = GameSceneController.playerOnameStatic;

        GameSceneController.mainMenuButtonStatic.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    appUser.window.loadMenuScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        playerXusername = GameSceneController.playerXusernameStatic;
        playerOusername = GameSceneController.playerOusernameStatic;

        playerXsign = GameSceneController.playerXsignStatic;
        playerOsign = GameSceneController.playerOsignStatic;

        try {
            makeUI();
            setCurrentPlayer(Player.PLAYER_X);
        } catch (Exception e) {
            e.printStackTrace();
        }
        repaintBoard();
        board.setDisable(false);
        try {
            appUser.window.loadGameScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void doAboutResult(Player result) {
        if (result != null) {
            board.setDisable(true);
        }
        if (result == Player.NONE) {
            massage.setText("Draw");
            massage.setFill(Color.PURPLE);
            playerXsign.setFill(Color.PURPLE);
            playerXname.setFill(Color.PURPLE);
            playerOsign.setFill(Color.PURPLE);
            playerOname.setFill(Color.PURPLE);
        }
        if (result == Player.PLAYER_X) {
            massage.setText("Player X won.");
            massage.setFill(Color.GREENYELLOW);
            playerXsign.setFill(Color.GREENYELLOW);
            playerXname.setFill(Color.GREENYELLOW);

            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
        }
        if (result == Player.PLAYER_O) {
            massage.setText("Player O won.");
            massage.setFill(Color.GREENYELLOW);
            playerOsign.setFill(Color.GREENYELLOW);
            playerOname.setFill(Color.GREENYELLOW);

            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        }

    }

    protected void setPlayersInfo(String xName, String oName, String xUsername, String oUsername) {
        playerOname.setText(oName);
        playerXname.setText(xName);
        playerOusername.setText(oUsername);
        playerXusername.setText(xUsername);
    }

    abstract void handleClick(MouseEvent mouseEvent);

    public void makeUI() throws Exception {
        squareSkins = new SquareSkin[3][3];

        gameScene = appUser.window.getGameScene();
        massage.setText("");

        //SplitPane splitPane = (SplitPane) appUser.window.getGameScene().lookup("#boardContainer");
        board = GameSceneController.boardStatic;
        //board.resize(WIDTH, HEIGHT);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squareSkins[i][j] = new SquareSkin(i, j);
                SquareSkin squareSkin = squareSkins[i][j];

                board.add(squareSkin, i, j);

                squareSkin.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    final Timeline timeline = new Timeline();

                    @Override
                    public void handle(javafx.scene.input.MouseEvent event) {
                        /*System.out.println("I'm clicked...");

                          double normalSize = square.getHeight();
                      timeline.getKeyFrames().addAll(

                                new KeyFrame(Duration.ZERO, // set start position at 0
                                        new KeyValue(square.backgroundProperty(), Ba),
                                        new KeyValue(square.prefWidthProperty(), normalSize)
                                ),
                                new KeyFrame(new Duration(2000), // set end position at 40s
                                        new KeyValue(square.maxHeightProperty(), normalSize - 10),
                                        new KeyValue(square.maxWidthProperty(), normalSize - 10)
                                ),new KeyFrame(new Duration(4000), // set end position at 40s
                                        new KeyValue(square.minHeightProperty(), normalSize),
                                        new KeyValue(square.minWidthProperty(), normalSize)
                                )
                        );

                        // play 40s of animation
                        timeline.play();*/
                        handleClick(event);

                    }
                });


            }
        }


    }

    public void repaintBoard() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                board.getChildren().clear();

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        Button button = new Button();
                        if (squares[i][j].getState() == Player.PLAYER_X) {
                            squareSkins[i][j].setText("X");

                        } else if (squares[i][j].getState() == Player.PLAYER_O) {
                            squareSkins[i][j].setText("O");
                        }

                        squareSkins[i][j].setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                            @Override
                            public void handle(javafx.scene.input.MouseEvent event) {
                                handleClick(event);
                            }
                        });
                        board.add(squareSkins[i][j], i, j);
                    }
                }
            }
        });
    }

    @Override
    public void setCurrentPlayer(Player currentPlayer) {
        super.setCurrentPlayer(currentPlayer);
        if (currentPlayer == Player.PLAYER_O) {
            playerOsign.setFill(Color.web("#1a73e8"));
            playerOname.setFill(Color.web("#1a73e8"));

            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        } else if (currentPlayer == Player.PLAYER_X) {
            playerXsign.setFill(Color.web("#1a73e8"));
            playerXname.setFill(Color.web("#1a73e8"));

            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
        } else if (currentPlayer == Player.NONE) {
            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        }
    }
}
