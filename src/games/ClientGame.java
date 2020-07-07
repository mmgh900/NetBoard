package games;

import Serlizables.ClientProfile;
import Serlizables.Packet;
import Serlizables.Square;
import controllers.GameSceneController;
import controllers.ProfileViewWindow;
import gui.elements.ChatTab;
import gui.elements.SquareSkin;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import services.UpdateChatService;
import users.Client;
import users.Connection;

public class ClientGame extends GameWithUI {
    private final Connection connection;
    ListView<ClientProfile> onlineContacts;
    ListView<ClientProfile> friends;
    private Player thisPlayer;
    private TabPane contacts;
    private TabPane chats;
    private Button sendChat;
    private TextField textField;

    public ClientGame(Client client) {
        super(client);
        setPlayersInfo("", "", "", "");
        board.setDisable(true);
        gameMode = GameModes.ONLINE;
        setCurrentPlayer(Player.NONE);
        this.thisPlayer = thisPlayer;
        this.connection = client.connection;
        this.client = client;
        initializeTabs();


    }

    @Override
    void doAboutResult(Player result) {
        super.doAboutResult(result);
        if (result == thisPlayer) {
            client.getClientProfile().setTotalOnlineWins(client.getClientProfile().getTotalOnlineWins() + 1);
            client.sendProfileToServer();
        } else if (result != Player.NONE && result != null) {
            client.getClientProfile().setTotalOnlineLosses(client.getClientProfile().getTotalOnlineLosses() + 1);
            client.sendProfileToServer();
        }
    }

    public void startGame(ClientProfile playerX, ClientProfile playerO) {
        setPlayersInfo(playerX.getFirstName(), playerO.getFirstName(), "@" + playerX.getUsername().toLowerCase(), "@" + playerO.getUsername().toLowerCase());
        board.setDisable(false);
        if (playerX.getUsername().toLowerCase().equals(client.getClientProfile().getUsername().toLowerCase())) {
            thisPlayer = Player.PLAYER_X;
        } else if (playerO.getUsername().toLowerCase().equals(client.getClientProfile().getUsername().toLowerCase())) {
            thisPlayer = Player.PLAYER_O;
        }
        setCurrentPlayer(Player.PLAYER_X);
    }

    public boolean isMyTurn() {
        return getCurrentPlayer() == thisPlayer;
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

    public void updateChats() {
        new UpdateChatService(chats, client).start();
/*        chats.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
                if (tab != null && t1 != null) {
                    //System.out.println("Changed from" + tab.getText() + " to " + t1.getText());
                    makeItZaro(t1);
                }

            }
        });


        for (Chat chat : client.getClientProfile().getChats()) {
            ChatTab foundTab = null;
            for (Tab tab : chats.getTabs()) {
                if (tab instanceof ChatTab && ((ChatTab) tab).getChat().equals(chat)) {
                    foundTab = (ChatTab) tab;
                }
            }
            if (foundTab == null) {
                foundTab = new ChatTab(chat, client);
                chats.getTabs().add(foundTab);
            }
            foundTab.refreshMassages(chat);
        }*/
    }

    private void makeItZaro(Tab t1) {
        ChatTab tab = (ChatTab) t1;
        ((ChatTab) t1).readAll();
    }

    public void update() {
        updateChats();
        updateOnlinesList();
        updateFriendsList();
    }


    private void initializeTabs() {
        chats = GameSceneController.chatsStatic;
        contacts = GameSceneController.contactsStatic;
        chats.getTabs().clear();
        updateChats();


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
            client.connection.sendPacket(new Packet(squares, client, Packet.PacketPropose.UPDATE_GAME));

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
