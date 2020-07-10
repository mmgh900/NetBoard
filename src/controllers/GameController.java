package controllers;

import Serlizables.Chat;
import Serlizables.ClientProfile;
import Serlizables.Massage;
import Serlizables.Square;
import games.GameWithUI;
import gui.elements.ChatTab;
import gui.elements.SquareSkin;
import gui.elements.TextMassageSkin;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static users.Server.ANSI_RED;
import static users.Server.ANSI_RESET;

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


    public ListView<ClientProfile> onlineContacts;
    public ListView<ClientProfile> friends;

    SquareSkin[][] squareSkins = new SquareSkin[3][3];
    Callback<ListView<ClientProfile>, ListCell<ClientProfile>> callback = new Callback<ListView<ClientProfile>, ListCell<ClientProfile>>() {
        @Override
        public ListCell<ClientProfile> call(ListView<ClientProfile> clientProfileListView) {
            ListCell<ClientProfile> cell = new ListCell<ClientProfile>() {
                @Override
                protected void updateItem(ClientProfile item, boolean empty) {
                    super.updateItem(item, empty);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            if (item != null) {
                                if (item.getOnline()) {
                                    setText(item.getUsername() + "(ONLINE)");
                                } else {
                                    setText(item.getUsername() + "(OFFLINE)");
                                }

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


        mainMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    client.getWindow().loadMenuScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        reset.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (client.game.getGameMode() != GameWithUI.GameMode.ONLINE) {
                    client.game.startGame(client.game.getGameMode());
                }
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

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                winPath.setVisible(false);
                board.setDisable((client.game.getGameMode() == GameWithUI.GameMode.NONE));
                reset.setVisible(((client.game.getGameMode() == GameWithUI.GameMode.MULTIPLAYER) || (client.game.getGameMode() == GameWithUI.GameMode.SINGLE_PLAYER)));
                contacts.setDisable((client.game.getGameMode() == GameWithUI.GameMode.MULTIPLAYER) || (client.game.getGameMode() == GameWithUI.GameMode.SINGLE_PLAYER));
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        squareSkins[i][j] = new SquareSkin(i, j);
                        SquareSkin squareSkin = squareSkins[i][j];


                        board.add(squareSkin, i, j);


                        squareSkin.setOnMouseClicked(new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(javafx.scene.input.MouseEvent event) {
                                client.game.handleClick(event);
                            }
                        });
                    }
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (squares[i][j].getState().equals(GameWithUI.Player.PLAYER_X)) {
                            squareSkins[i][j].setText("X");
                        } else if (squares[i][j].getState().equals(GameWithUI.Player.PLAYER_O)) {
                            squareSkins[i][j].setText("O");
                        } else if (squares[i][j].getState().equals(GameWithUI.Player.NONE)) {
                            squareSkins[i][j].setText("");
                        }
                    }
                }


            }
        });
    }

    public void setCurrentPlayer(GameWithUI.Player currentPlayer) {
        if (currentPlayer == GameWithUI.Player.PLAYER_O) {
            playerOsign.setFill(Color.web("#1a73e8"));
            playerOname.setFill(Color.web("#1a73e8"));

            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        } else if (currentPlayer == GameWithUI.Player.PLAYER_X) {
            playerXsign.setFill(Color.web("#1a73e8"));
            playerXname.setFill(Color.web("#1a73e8"));

            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
        } else if (currentPlayer == GameWithUI.Player.NONE) {
            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        }
    }

    public void initializeTabs() {
        chats = chats;

        //Listens to tab changes to seting unread massages zero
        ChangeListener<Tab> tabChangeListener = new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
                if (tab != null && t1 != null) {
                    makeItZero(t1);
                }

            }
        };
        chats.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);

        initializeChats();

        contacts.getTabs().clear();
        Tab friendsTab = new Tab("Friends");
        Tab onlineClientsTab = new Tab("Onlines");

        friends = new ListView<ClientProfile>();
        onlineContacts = new ListView<ClientProfile>();


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
        ChatTab tab = (ChatTab) t1;
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

    public void updateChats(ClientProfile clientProfile) {
        int numberOfLocalChats = client.getClientProfile().getChats().size();
        int numberOfServerChats = clientProfile.getChats().size();

        if (numberOfLocalChats == numberOfServerChats) {
            //System.out.println(ANSI_RED + "\t" + clientProfile.toString() + "numberOfLocalChats= " + numberOfLocalChats + " numberOfServerChats= " + numberOfServerChats + " so no chat added" + ANSI_RESET);
        } else if (numberOfServerChats < numberOfLocalChats) {
            try {
                throw new Exception("ERROR: SERVER CHATS MUST BE MORE THAN LOCAL CHATS");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = numberOfLocalChats; i < numberOfServerChats; i++) {
            Chat chat = clientProfile.getChats().get(i);
            client.getClientProfile().getChats().add(chat);
            addChatTab(chat);
            for (Massage massage : clientProfile.getChats().get(i).getMassages()) {
                addMassageToChat(massage, chat, client.getClientProfile().getChats().indexOf(chat));
            }
        }

        for (int i = 0; i < numberOfLocalChats; i++) {
            System.out.println("im in");
            int numberOfLocalMassages = client.getClientProfile().getChats().get(i).getMassages().size();
            int numberOfServerMassages = clientProfile.getChats().get(i).getMassages().size();

            if (numberOfLocalMassages < numberOfServerMassages) {
                for (int j = numberOfLocalMassages; j < numberOfServerMassages; j++) {
                    client.getClientProfile().getChats().get(i).getMassages().add(clientProfile.getChats().get(i).getMassages().get(j));
                    addMassageToChat(clientProfile.getChats().get(i).getMassages().get(j), client.getClientProfile().getChats().get(i), i);
                    //System.out.println(ANSI_RED + "\t" + clientProfile.toString() + "numberOfLocalMassages= " + numberOfLocalMassages + " numberOfServerMassages= " + numberOfServerMassages + " so massage added" + ANSI_RESET);
                }
            } else if (numberOfLocalMassages == numberOfServerMassages) {
                System.out.println(ANSI_RED + "\t" + clientProfile.toString() + "numberOfLocalMassages= " + numberOfLocalMassages + " numberOfServerMassages= " + numberOfServerMassages + " so NO massage added" + ANSI_RESET);
            } else
                try {
                    throw new Exception("ERROR: SERVER MASSAGES MUST BE MORE THAN LOCAL CHATS");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public void initializeChats() {
        chats.getTabs().clear();
        for (Chat chat : client.getClientProfile().getChats()) {
            addChatTab(chat);
            for (Massage massage : chat.getMassages()) {
                addMassageToChat(massage, chat, client.getClientProfile().getChats().indexOf(chat));
            }
        }
    }

    public void doAboutResult(GameWithUI.Player result) {
        setCurrentPlayer(GameWithUI.Player.NONE);
        if (client.game.getWinPathNumbers() != null) {
            markPath(client.game.getWinPathNumbers()[0], client.game.getWinPathNumbers()[1], client.game.getWinPathNumbers()[2], client.game.getWinPathNumbers()[3]);
        }
        if (result != null) {
            board.setDisable(true);
        }
        if (result == GameWithUI.Player.NONE) {
            massage.setText("Draw");
            massage.setFill(Color.PURPLE);
            playerXsign.setFill(Color.PURPLE);
            playerXname.setFill(Color.PURPLE);
            playerOsign.setFill(Color.PURPLE);
            playerOname.setFill(Color.PURPLE);
        }
        if (result == GameWithUI.Player.PLAYER_X) {
            massage.setText("Player X won.");
            massage.setFill(Color.GREENYELLOW);
            playerXsign.setFill(Color.GREENYELLOW);
            playerXname.setFill(Color.GREENYELLOW);

            playerOsign.setFill(Color.GREY);
            playerOname.setFill(Color.GREY);
        }
        if (result == GameWithUI.Player.PLAYER_O) {
            massage.setText("Player O won.");
            massage.setFill(Color.GREENYELLOW);
            playerOsign.setFill(Color.GREENYELLOW);
            playerOname.setFill(Color.GREENYELLOW);

            playerXsign.setFill(Color.GREY);
            playerXname.setFill(Color.GREY);
        }

    }

    public void setPlayersInfo(ClientProfile playerXProfile, ClientProfile playerOProfile) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playerOname.setText(playerOProfile.getFirstName());
                playerXname.setText(playerXProfile.getFirstName());
                playerOusername.setText("@" + playerOProfile.getUsername().toLowerCase());
                playerXusername.setText("@" + playerXProfile.getUsername().toLowerCase());
                playerOViewProfile.setVisible(true);
                playerXViewProfile.setVisible(true);
                playerXViewProfile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        new ProfileViewWindow(client, playerXProfile);
                    }
                });
                playerOViewProfile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        new ProfileViewWindow(client, playerOProfile);
                    }
                });
            }
        });

    }

    public void addMassageToChat(Massage massage, Chat chat, int chatIndex) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ChatTab chatTab = (ChatTab) chats.getTabs().get(chatIndex);
                boolean isSelf = (massage.getSender().equals(client.getClientProfile()));
                chatTab.getMassages().getChildren().add(new TextMassageSkin(isSelf, massage));
                if (!chatTab.getTabPane().getSelectionModel().getSelectedItem().equals(chatTab)) {
                    chatTab.addUnReadMassages();
                }
                chatTab.getChatController().scrollPane.setVvalue(1.0);
            }
        });

    }

    public void addChatTab(Chat chat) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chats.getTabs().add(new ChatTab(chat, client));
            }
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
