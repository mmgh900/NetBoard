package gui.elements;

import Serlizables.Chat;
import Serlizables.Massage;
import Serlizables.Packet;
import controllers.ChatBoxController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import users.Client;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ChatTab extends Tab {
    private ChatTab thisChatTab;
    private Chat chat;
    private final VBox massages;
    private final Client client;
    private ChatBoxController chatBoxController;
    private int unReadMassages = 0;
    private Date lastUpdate;
    public ChatTab(Chat chat, Client client) {

        //this.setStyle("-fx-pref-width: 250");
        this.chat = chat;
        System.out.println(chat.getName());
        this.setText(chat.getName());
        this.client = client;
        this.setUserData(chat);
        this.lastUpdate = new Date();
        FXMLLoader chatBoxLoader = new FXMLLoader();
        AnchorPane anchorPane = null;

        try {
            chatBoxLoader.setLocation(new File("resources/FXMLFiles/ChatBox.fxml").toURL());
            anchorPane = chatBoxLoader.load();
            chatBoxController = chatBoxLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContent(anchorPane);
        massages = chatBoxController.massages;
        chatBoxController.send.setOnMouseClicked(sendMassage);
        chatBoxController.send.setStyle("-fx-font-size: 15");
        chatBoxController.send.setText(chat.getName().substring(0, 1));
        refreshMassages(chat);

    }

    EventHandler<MouseEvent> sendMassage = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            String text = chatBoxController.textField.getText();
            if (!text.isBlank()) {
                Massage massage = new Massage(client.getClientProfile(), new Date(), chatBoxController.textField.getText());
                client.connection.sendPacket(new Packet(massage, client.getClientProfile(), chat.getMembers().get(1), Packet.PacketPropose.CHAT));
            }
        }
    };

    public void addUnReadMassages() {
        unReadMassages++;
        setText(chat.getName() + "(" + unReadMassages + ")");
    }

    public void refreshMassages(Chat chat) {
        thisChatTab = this;
        this.chat = chat;
        this.setUserData(chat);
        //new UpdateSingleChatService(this, chat.getMassages(), client).start();
/*        for (Massage massage : chat.getMassages()) {
            boolean exists = false;
            for (Node node : massages.getChildren()) {
                if (node instanceof TextMassageSkin && ((TextMassageSkin) node).getMassage().equals(massage)) {
                    exists = true;
                }
            }
            if (!exists) {
                boolean isSelf = (massage.getSender().equals(client.getClientProfile()));
                massages.getChildren().add(new TextMassageSkin(isSelf, massage));
                if (getTabPane() != null && !getTabPane().getSelectionModel().getSelectedItem().equals(thisChatTab)) {
                    unReadMassages++;
                    setText(chat.getName() + "(" + unReadMassages + ")");
                }
                chatBoxController.scrollPane.setVvalue(1.0);
            }

        }*/

    }

    public Chat getChat() {
        return chat;
    }

    public VBox getMassages() {
        return massages;
    }

    public void readAll() {

        unReadMassages = 0;
        setText(chat.getName());


    }

    public ChatTab getThisChatTab() {
        return thisChatTab;
    }

    public Client getClient() {
        return client;
    }

    public ChatBoxController getChatBoxController() {
        return chatBoxController;
    }

    public int getUnReadMassages() {
        return unReadMassages;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
