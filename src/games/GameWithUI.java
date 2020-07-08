package games;

import Serlizables.Chat;
import Serlizables.ClientProfile;
import Serlizables.Massage;
import Serlizables.SecurityQuestions;
import controllers.GameController;
import controllers.ProfileViewWindow;
import gui.elements.ChatTab;
import gui.elements.SquareSkin;
import gui.elements.TextMassageSkin;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import users.Client;

import java.io.IOException;

import static users.Server.ANSI_RED;
import static users.Server.ANSI_RESET;

public abstract class GameWithUI extends Game {

    public static ClientProfile me = new ClientProfile("Mohammad Mahdi", "Gheysari", "mmgh900", "1234", SecurityQuestions.WHO_WAS_YOUR_CHILDHOOD_HERO, "Batman", "gheysari.mm@gmail.com");
    protected TabPane contacts;
    protected TabPane chats;

    protected ListView<ClientProfile> onlineContacts;
    protected ListView<ClientProfile> friends;


    protected Scene gameScene;
    protected GridPane board;

    protected Client client;
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

    public GameWithUI(Client client) {
        super();
        this.client = client;
        this.massage = GameController.massageStatic;
        playerXname = GameController.playerXnameStatic;
        playerOname = GameController.playerOnameStatic;

        GameController.mainMenuButtonStatic.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    client.getWindow().loadMenuScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        playerXusername = GameController.playerXusernameStatic;
        playerOusername = GameController.playerOusernameStatic;

        playerXsign = GameController.playerXsignStatic;
        playerOsign = GameController.playerOsignStatic;

        try {
            makeUI();
            setCurrentPlayer(Player.PLAYER_X);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeTabs();
        repaintBoard();


        board.setDisable(false);

    }

    public void updateOnlinesList() {
        for (ClientProfile clientProfile : client.getOtherPlayers()) {
            ClientProfile foundClient = null;
            for (ClientProfile clientProfile1 : onlineContacts.getItems()) {
                if (clientProfile.equals(clientProfile1)) {
                    foundClient = clientProfile1 = clientProfile;
                }
            }
            if (foundClient == null && clientProfile.getOnline()) {
                onlineContacts.getItems().add(clientProfile);
            } else if (clientProfile != null && !clientProfile.getOnline()) {
                onlineContacts.getItems().remove(foundClient);
            }
        }
        onlineContacts.refresh();
    }

    public void updateFriendsList() {
        friends.getItems().clear();
        for (ClientProfile clientProfile : client.getClientProfile().getFriends()) {
            friends.getItems().add(clientProfile);
        }
        friends.refresh();
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
        for (Chat chat : client.getClientProfile().getChats()) {
            addChatTab(chat);
            for (Massage massage : chat.getMassages()) {
                addMassageToChat(massage, chat, client.getClientProfile().getChats().indexOf(chat));
            }
        }
    }
/*
    public void updateChats() {
        //new UpdateChatService(chats, client).start();

    }
*/

    private void makeItZero(Tab t1) {
        ChatTab tab = (ChatTab) t1;
        ((ChatTab) t1).readAll();
    }

    public void update() {
        //updateChats();
        updateOnlinesList();
        updateFriendsList();
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

    private void initializeTabs() {
        chats = GameController.chatsStatic;

        ChangeListener<Tab> tabChangeListener = new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
                if (tab != null && t1 != null) {
                    //System.out.println("Changed from" + tab.getText() + " to " + t1.getText());
                    makeItZero(t1);
                }

            }
        };


        chats.getSelectionModel().selectedItemProperty().addListener(tabChangeListener);

        contacts = GameController.contactsStatic;
        chats.getTabs().clear();
        initializeChats();
        //updateChats();


        contacts.getTabs().clear();

        Tab friendsTab = new Tab("Friends");
        Tab onlineClientsTab = new Tab("Onlines");


        friends = new ListView<ClientProfile>();
        onlineContacts = new ListView<ClientProfile>();

        onlineContacts.setCellFactory(lv -> {
            ListCell<ClientProfile> cell = new ListCell<ClientProfile>() {

                @Override
                protected void updateItem(ClientProfile item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.getUsername());
                    }
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
        });
        friends.setCellFactory(lv -> {
            ListCell<ClientProfile> cell = new ListCell<ClientProfile>() {

                @Override
                protected void updateItem(ClientProfile item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item != null) {
                        setText(item.getUsername());
                    }
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
        });


        onlineClientsTab.setContent(onlineContacts);
        friendsTab.setContent(friends);

        contacts.getTabs().add(onlineClientsTab);
        contacts.getTabs().add(friendsTab);
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

        gameScene = client.getWindow().getGameScene();
        massage.setText("");

        //SplitPane splitPane = (SplitPane) client.getWindow().getGameScene().lookup("#boardContainer");
        board = GameController.boardStatic;
        //board.resize(WIDTH, HEIGHT);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        squareSkins[i][j] = new SquareSkin(i, j);
                        SquareSkin squareSkin = squareSkins[i][j];


                        board.add(squareSkin, i, j);

                        squareSkin.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            final Timeline timeline = new Timeline();

                            @Override
                            public void handle(javafx.scene.input.MouseEvent event) {
                                handleClick(event);

                            }
                        });


                    }
                }
            }
        });


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
