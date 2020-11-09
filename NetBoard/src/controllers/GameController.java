package controllers;

import games.TicTacToe;
import gui.elements.ChatTab;
import gui.elements.SquareSkin;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Callback;
import serlizables.Chat;
import serlizables.ClientProfile;
import serlizables.Packet;
import serlizables.Square;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GameController extends StandardController implements Initializable {


    public AnchorPane rootPane;
    public GridPane board;
    public TabPane contacts;
    public TabPane chats;
    public Text massage;
    public Text playerXname;
    public Text playerOname;
    public Text playerXusername;
    public Text playerOusername;
    public Circle playerXsign;
    public Circle playerOsign;
    public Button playerXViewProfile;
    public Button playerOViewProfile;
    public Button mainMenuButton;
    public Button reset;
    public AnchorPane underBoard;
    public Line winPath;

    public Button searchButton;
    public TextField searchField;


    public ListView<ClientProfile> onlineContacts;
    public ListView<ClientProfile> friends;

    SquareSkin[][] squareSkins = new SquareSkin[3][3];
    Callback<ListView<ClientProfile>, ListCell<ClientProfile>> callback = new Callback<>() {
        @Override
        public ListCell<ClientProfile> call(ListView<ClientProfile> clientProfileListView) {
            ListCell<ClientProfile> cell = new ListCell<>() {
                @Override
                protected void updateItem(ClientProfile item, boolean empty) {
                    super.updateItem(item, empty);
                    Platform.runLater(() -> {
                        if (item != null) {
                            if (item.getOnline()) {
                                setText(item.getUsername() + " (Online)");
                            } else {
                                setText(item.getUsername() + " (Offline)");
                            }

                        }
                    });

                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    if (e.getClickCount() == 2) {
                        new ProfileViewWindow(client, cell.getItem());
                    }

                    e.consume();
                }
            });

            return cell;
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        clearGameScene();

        mainMenuButton.setOnMouseClicked(event -> client.getWindow().loadMenuScene());
        reset.setOnMouseClicked(mouseEvent -> {
            if (client.game.getGameMode() != TicTacToe.GameMode.ONLINE) {
                client.game.startGame(client.game.getGameMode());
            }
        });

        searchButton.setOnMouseClicked(mouseEvent -> {
            String username = searchField.getText();
            if (!username.isEmpty() && !username.isBlank()) {
                client.socketStreamManager.sendPacket(new Packet(username, client.getClientProfile(), Packet.PacketPropose.SEARCH_USERNAME));
            }
        });
    }

    public void clearGameScene() {
        playerOViewProfile.setVisible(false);
        playerXViewProfile.setVisible(false);
        board.getChildren().clear();
        massage.setText("");
        setPlayersInfo("", "", "", "");
    }

    public void makeNewBoardSkin() {

        Platform.runLater(() -> {
            winPath.setVisible(false);
            board.setDisable((client.game.getGameMode() == TicTacToe.GameMode.NONE));
            reset.setVisible(((client.game.getGameMode() == TicTacToe.GameMode.MULTIPLAYER) || (client.game.getGameMode() == TicTacToe.GameMode.SINGLE_PLAYER)));
            contacts.setDisable((client.game.getGameMode() == TicTacToe.GameMode.MULTIPLAYER) || (client.game.getGameMode() == TicTacToe.GameMode.SINGLE_PLAYER));
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    squareSkins[i][j] = new SquareSkin(i, j);
                    SquareSkin squareSkin = squareSkins[i][j];


                    board.add(squareSkin, i, j);


                    squareSkin.setOnMouseClicked(event -> client.game.handleClick(event));
                }
            }
        });
    }

    public void markPath(int startI, int startJ, int endI, int endJ) {
        winPath.setStartX(90 + startI * 165);
        winPath.setStartY(90 + startJ * 165);
        winPath.setEndX(90 + endI * 165);
        winPath.setEndY(90 + endJ * 165);
        winPath.setVisible(true);
    }

    public void repaintBoard(Square[][] squares) {
        Platform.runLater(() -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (squares[i][j].getState().equals(TicTacToe.Player.PLAYER_X)) {
                        squareSkins[i][j].setText("X");
                    } else if (squares[i][j].getState().equals(TicTacToe.Player.PLAYER_O)) {
                        squareSkins[i][j].setText("O");
                    } else if (squares[i][j].getState().equals(TicTacToe.Player.NONE)) {
                        squareSkins[i][j].setText("");
                    }
                }
            }


        });
    }

    public void setCurrentPlayer(TicTacToe.Player currentPlayer) {
        if (currentPlayer == TicTacToe.Player.PLAYER_O) {
            playerOsign.setFill(Color.web("#1a73e8"));
            playerOname.setFill(Color.web("#1a73e8"));

            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        } else if (currentPlayer == TicTacToe.Player.PLAYER_X) {
            playerXsign.setFill(Color.web("#1a73e8"));
            playerXname.setFill(Color.web("#1a73e8"));

            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
        } else if (currentPlayer == TicTacToe.Player.NONE) {
            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        }
    }

    public void initializeTabs() {
        //Listens to tab changes to seting unread massages zero
        ChangeListener<Tab> tabChangeListener = (observableValue, tab, t1) -> {
            if (tab != null && t1 != null) {
                makeItZero(t1);
            }

        };
        chats.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);

        initializeChats();

        contacts.getTabs().clear();
        Tab friendsTab = new Tab("Friends");
        Tab onlineClientsTab = new Tab("Onlines");

        friends = new ListView<>();
        onlineContacts = new ListView<>();


        onlineClientsTab.setContent(onlineContacts);
        friendsTab.setContent(friends);

        initializeContactList(onlineContacts, client.getOnlineClients());
        initializeContactList(friends, client.getClientProfile().getFriends());

        contacts.getTabs().add(onlineClientsTab);
        contacts.getTabs().add(friendsTab);

    }

    private void initializeContactList(ListView<ClientProfile> listSkin, ArrayList<ClientProfile> list) {
        listSkin.setCellFactory(callback);
        listSkin.getItems().clear();
        for (ClientProfile clientProfile : list) {
            listSkin.getItems().add(clientProfile);
        }
    }

    private void makeItZero(Tab t1) {
        ((ChatTab) t1).readAll();
    }

    public void updateFriendsList() {
        friends.getItems().clear();
        for (ClientProfile clientProfile : client.getClientProfile().getFriends()) {
            friends.getItems().add(clientProfile);
        }
        friends.refresh();
    }

    public void setPlayersInfo(String xName, String oName, String xUsername, String oUsername) {

        playerOname.setText(oName);
        playerXname.setText(xName);
        playerOusername.setText(oUsername);
        playerXusername.setText(xUsername);
        playerXViewProfile.setVisible(false);
        playerOViewProfile.setVisible(false);
    }

    public void initializeChats() {
        chats.getTabs().clear();
        for (Chat chat : client.getClientProfile().getChats()) {
            new ChatTab(chat, client);
        }

    }

    public void doAboutResult(TicTacToe.Player result) {
        setCurrentPlayer(TicTacToe.Player.NONE);
        if (client.game.getWinPathNumbers() != null) {
            markPath(client.game.getWinPathNumbers()[0], client.game.getWinPathNumbers()[1], client.game.getWinPathNumbers()[2], client.game.getWinPathNumbers()[3]);
        }
        if (result != null) {
            board.setDisable(true);
        }
        if (result == TicTacToe.Player.NONE) {
            massage.setText("Draw");
            massage.setFill(Color.PURPLE);
            playerXsign.setFill(Color.PURPLE);
            playerXname.setFill(Color.PURPLE);
            playerOsign.setFill(Color.PURPLE);
            playerOname.setFill(Color.PURPLE);
        }
        if (result == TicTacToe.Player.PLAYER_X) {
            massage.setText("Player X won.");
            massage.setFill(Color.GREENYELLOW);
            playerXsign.setFill(Color.GREENYELLOW);
            playerXname.setFill(Color.GREENYELLOW);

            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
        }
        if (result == TicTacToe.Player.PLAYER_O) {
            massage.setText("Player O won.");
            massage.setFill(Color.GREENYELLOW);
            playerOsign.setFill(Color.GREENYELLOW);
            playerOname.setFill(Color.GREENYELLOW);

            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        }

    }

    public void setPlayersInfo(ClientProfile playerXProfile, ClientProfile playerOProfile) {

        Platform.runLater(() -> {
            playerOname.setText(playerOProfile.getFirstName());
            playerXname.setText(playerXProfile.getFirstName());
            playerOusername.setText("@" + playerOProfile.getUsername().toLowerCase());
            playerXusername.setText("@" + playerXProfile.getUsername().toLowerCase());
            playerOViewProfile.setVisible(true);
            playerXViewProfile.setVisible(true);
            playerXViewProfile.setOnMouseClicked(mouseEvent -> new ProfileViewWindow(client, playerXProfile));
            playerOViewProfile.setOnMouseClicked(mouseEvent -> new ProfileViewWindow(client, playerOProfile));
        });

    }


    public void aClientChangedStatus(ClientProfile clientProfile) {
        for (ClientProfile clientProfile1 : client.getClientProfile().getFriends()) {
            if (clientProfile1.equals(clientProfile)) {
                clientProfile1.setOnline(clientProfile.getOnline());
            }
        }
        if (!clientProfile.getOnline()) {
            client.getOnlineClients().remove(clientProfile);
            onlineContacts.getItems().remove(clientProfile);
        } else {
            client.getOnlineClients().add(clientProfile);
            onlineContacts.getItems().add(clientProfile);
        }
        onlineContacts.refresh();
        friends.refresh();
    }

}
