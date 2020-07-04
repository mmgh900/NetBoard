package games;

import Serlizables.*;
import controllers.AppUser;
import controllers.ChatBoxController;
import controllers.GameSceneController;
import controllers.ProfileViewWindow;
import gui.elements.SquareSkin;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import users.Connection;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

public class ClientGame extends GameWithUI {
    private final Connection connection;
    ListView<ClientProfile> onlineContacts;
    ListView<ClientProfile> friends;
    private Player thisPlayer;
    private TabPane contacts;
    private TabPane chats;
    private Button sendChat;
    private TextField textField;

    public ClientGame(AppUser appUser, Connection connection) {
        super(appUser);
        setPlayersInfo("", "", "", "");
        board.setDisable(true);
        gameMode = GameModes.ONLINE;
        setCurrentPlayer(Player.NONE);
        this.thisPlayer = thisPlayer;
        this.connection = connection;
        initializeTabs();


    }

    @Override
    void doAboutResult(Player result) {
        super.doAboutResult(result);
        if (result == thisPlayer) {
            appUser.client.getClientProfile().setTotalOnlineWins(appUser.client.getClientProfile().getTotalOnlineWins() + 1);
            appUser.client.sendProfileToServer();
        } else if (result != Player.NONE && result != null) {
            appUser.client.getClientProfile().setTotalOnlineLosses(appUser.client.getClientProfile().getTotalOnlineLosses() + 1);
            appUser.client.sendProfileToServer();
        }
    }

    public void startGame(ClientProfile playerX, ClientProfile playerO) {
        setPlayersInfo(playerX.getFirstName(), playerO.getFirstName(), "@" + playerX.getUserInfo().getUsername().toLowerCase(), "@" + playerO.getUserInfo().getUsername().toLowerCase());
        board.setDisable(false);
        if (playerX.getUserInfo().getUsername().toLowerCase().equals(appUser.client.userInfo.getUsername().toLowerCase())) {
            thisPlayer = Player.PLAYER_X;
        } else if (playerO.getUserInfo().getUsername().toLowerCase().equals(appUser.client.userInfo.getUsername().toLowerCase())) {
            thisPlayer = Player.PLAYER_O;
        }
        setCurrentPlayer(Player.PLAYER_X);
    }

    public boolean isMyTurn() {
        return getCurrentPlayer() == thisPlayer;
    }

    private void updateOnlinesList() {
        onlineContacts.getItems().clear();
        for (ClientProfile clientProfile : appUser.client.getOtherPlayers()) {
            if (clientProfile.getOnline()) {
                onlineContacts.getItems().add(clientProfile);
            }

        }
        onlineContacts.refresh();
    }

    private void updateFriendsList() {
        friends.getItems().clear();
        for (ClientProfile clientProfile : appUser.client.getClientProfile().getFriends()) {
            friends.getItems().add(clientProfile);
        }
        friends.refresh();
    }

    private void updateChats() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chats.getTabs().clear();
                for (Chat chat : appUser.client.getClientProfile().getChats()) {
                    ClientProfile otherOne = null;
                    if (chat.getMembers().get(0).equals(appUser.client.getClientProfile())) {
                        otherOne = chat.getMembers().get(1);
                        chat.setName(chat.getMembers().get(1).getUserInfo().getUsername().toLowerCase());
                    } else if (chat.getMembers().get(1).equals(appUser.client.getClientProfile())) {
                        otherOne = chat.getMembers().get(0);
                        chat.setName(chat.getMembers().get(0).getUserInfo().getUsername().toLowerCase());
                    }
                    AnchorPane anchorPane = null;
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    ChatBoxController chatBoxController = null;
                    try {
                        fxmlLoader.setLocation(new File("resources/FXMLFiles/ChatBox.fxml").toURL());
                        anchorPane = fxmlLoader.load();
                        chatBoxController = fxmlLoader.getController();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Tab tab = new Tab(chat.getName());
                    tab.setContent(anchorPane);
                    chats.getTabs().add(tab);
                    VBox massages = chatBoxController.massages;
                    massages.getChildren().clear();
                    for (Massage massage : chat.getMassages()) {
                        massages.getChildren().add(newMassageBox(massage, appUser.client.getClientProfile()));
                    }
                    ChatBoxController chatBoxController1 = chatBoxController;
                    ClientProfile otherOne1 = otherOne;
                    chatBoxController.send.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            String text = chatBoxController1.textField.getText();
                            if (!text.isBlank()) {
                                Massage massage = new Massage(appUser.client.getClientProfile(), new Date(), chatBoxController1.textField.getText());
                                /*chat.getMassages().add(massage);
                                if (appUser.game instanceof ClientGame) {
                                    ((ClientGame) appUser.game).updateChats();
                                }
                                appUser.client.sendProfileToServer();
                                updateChats();*/
                                appUser.client.connection.sendPacket(new Packet(massage, appUser.client.getClientProfile(), otherOne1, Packet.PacketPropose.CHAT));
                            }
                        }
                    });
                }
            }
        });
    }

    public void update() {
        updateChats();
        updateOnlinesList();
        updateFriendsList();
    }

    public HBox newMassageBox(Massage massage, ClientProfile clientProfile) {
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(5, 10, 5, 10));
        hBox.setPrefWidth(395);
        Button button = new Button();
        button.setWrapText(true);
        button.setText(massage.getContent());
        Text text = new Text(massage.getDate().getHours() + ":" + massage.getDate().getMinutes());
        button.getStyleClass().add("chat");

        hBox.getChildren().add(button);
        button.getStyleClass().add("chat");
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(text);
        if (massage.getSender().equals(clientProfile)) {
            hBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        } else {
            hBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            button.setStyle("-fx-background-color: dimgray");
        }


        return hBox;
    }

    private void initializeTabs() {
        chats = GameSceneController.chatsStatic;
        contacts = GameSceneController.contactsStatic;
        updateChats();
        contacts.getTabs().clear();
        Tab onlineClientsTab = new Tab("Onlines");
        Tab friendsTab = new Tab("Friends");

        friends = new ListView<>();
        onlineContacts = new ListView<>();

        onlineContacts.setCellFactory(lv -> {
            ListCell<ClientProfile> cell = new ListCell<ClientProfile>() {

                @Override
                protected void updateItem(ClientProfile item, boolean empty) {
                    super.updateItem(item, empty);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            if (item != null) {
                                setText(item.getUserInfo().getUsername());
                            }
                        }
                    });
                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getClickCount() == 2) {
                                new ProfileViewWindow(appUser.client, cell.getItem());
                            }

                        }
                    });
                    e.consume();
                }
            });

            return cell;
        });

        friends.setCellFactory(lv -> {
            ListCell<ClientProfile> cell = new ListCell<ClientProfile>() {

                @Override
                protected void updateItem(ClientProfile item, boolean empty) {
                    super.updateItem(item, empty);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            if (item != null) {
                                setText(item.getUserInfo().getUsername());
                            }
                        }
                    });
                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getClickCount() == 2) {
                                new ProfileViewWindow(appUser.client, cell.getItem());
                            }

                        }
                    });
                    e.consume();
                }
            });

            return cell;
        });


        onlineClientsTab.setContent(onlineContacts);
        friendsTab.setContent(friends);

        contacts.getTabs().add(friendsTab);
        contacts.getTabs().add(onlineClientsTab);
        //onlineContacts.getItems().add(me);
    }

    public void updateGame(Square[][] squares) {
        this.squares = squares;
        setCurrentPlayer(thisPlayer);
        repaintBoard();
        doAboutResult(checkResult());
    }

    @Override
    void handleClick(MouseEvent mouseEvent) {
        SquareSkin squareSkin = (SquareSkin) mouseEvent.getSource();
        Square square = squares[squareSkin.getX()][squareSkin.getY()];

        if (isMyTurn() && square.getState().equals(Player.NONE)) {
            square.setState(getCurrentPlayer());
            appUser.client.connection.sendPacket(new Packet(squares, appUser.client, Packet.PacketPropose.UPDATE_GAME));

            if (thisPlayer == Player.PLAYER_X) {
                setCurrentPlayer(Player.PLAYER_O);
            } else if (thisPlayer == Player.PLAYER_O) {
                setCurrentPlayer(Player.PLAYER_X);
            }
            repaintBoard();
            doAboutResult(checkResult());
        } else if (!isMyTurn()) {
            massage.setText("It's not your turn.");
        }
    }
}
