package gui.elements;

import Serlizables.Chat;
import Serlizables.Massage;
import Serlizables.Packet;
import controllers.ChatController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import users.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class ChatTab extends Tab {
    private ChatTab thisChatTab;
    private Chat chat;
    private final VBox massages;
    private final Client client;
    private ChatController chatController;
    private int unReadMassages = 0;
    private Date lastUpdate;
    EventHandler<MouseEvent> sendMassage = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            String text = chatController.textField.getText();
            if (!text.isBlank()) {
                Massage massage = new Massage(chat, client.getClientProfile(), new Date(), chatController.textField.getText(), Massage.MassageType.TEXT);
                client.connection.sendPacket(new Packet(massage, client.getClientProfile(), chat.getMembers().get(1), Packet.PacketPropose.CHAT));
                chatController.textField.clear();
            }
        }
    };
    EventHandler<MouseEvent> sendFile = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {

            FileInputStream fileInputStream = null;
            try {
                FileChooser fileChooser = new FileChooser();
                File[] selectedFile = new File[1];

                selectedFile[0] = fileChooser.showOpenDialog(chatController.sendFile.getScene().getWindow());


                if (selectedFile[0] != null) {
                    if (!selectedFile[0].exists()) {
                        throw new Exception("file is not found");
                    }
                    fileInputStream = new FileInputStream(selectedFile[0]);
                    byte[] bytes = fileInputStream.readAllBytes();

                    Massage massage = new Massage(chat, client.getClientProfile(), new Date(), selectedFile[0].getPath(), Massage.MassageType.IMAGE);
                    chat.getMassages().add(massage);
                    client.game.getGameController().addMassageToChat(massage);

                    System.out.println("File length = " + selectedFile[0].length() + " and bytes length = " + bytes.length);
                    client.connection.sendPacket(new Packet(bytes, selectedFile[0].getName(), client.getClientProfile(), chat.getMembers().get(1), Packet.PacketPropose.FILE));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

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
            chatController = chatBoxLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContent(anchorPane);
        massages = chatController.massages;
        chatController.send.setOnMouseClicked(sendMassage);
        chatController.sendFile.setOnMouseClicked(sendFile);
        chatController.send.setStyle("-fx-font-size: 15");
        chatController.send.setText(chat.getName().substring(0, 1));
        refreshMassages(chat);

    }

    public void addUnReadMassages() {
        unReadMassages++;
        setText(chat.getName() + "(" + unReadMassages + " UNREAD)");
    }

    public void refreshMassages(Chat chat) {
        thisChatTab = this;
        this.chat = chat;
        this.setUserData(chat);
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

    public ChatController getChatController() {
        return chatController;
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
