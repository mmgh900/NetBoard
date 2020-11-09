package gui.elements;

import controllers.ChatController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import serlizables.Chat;
import serlizables.Massage;
import serlizables.Packet;
import users.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ChatTab extends Tab {
    private ChatTab thisChatTab;
    private Chat chat;

    private final VBox massages;
    private final Client client;

    private ChatController chatController;

    private int unReadMassages = 0;

    private final Date lastUpdate;

    EventHandler<MouseEvent> sendMassage = mouseEvent -> sendTextMassage();
    EventHandler<MouseEvent> sendFile = mouseEvent -> sendFileMassage();

    public ChatTab(Chat chat, Client client) {
        this.chat = chat;
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


        for (Massage massage : chat.getMassages()) {
            addMassageToChat(massage);
        }

        Platform.runLater(() -> client.game.getGameController().chats.getTabs().add(thisChatTab));


    }

    //Send methods
    private void sendFileMassage() {
        //Getting the file and make a byte array out of it
        byte[] bytes = null;
        File selectedFile = null;
        FileInputStream fileInputStream;
        try {
            FileChooser fileChooser = new FileChooser();
            selectedFile = fileChooser.showOpenDialog(chatController.sendFile.getScene().getWindow());
            if (selectedFile != null) {
                if (!selectedFile.exists()) {
                    throw new Exception("file is not found");
                }
                fileInputStream = new FileInputStream(selectedFile);
                bytes = fileInputStream.readAllBytes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (selectedFile == null) {
            throw new NullPointerException();
        }

        //Creat massage, add to chats array list, add to massagesBox, send massage to server, send profile to server
        String format = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
        Massage.MassageType massageType = null;
        if (format.equalsIgnoreCase(".png") || format.equalsIgnoreCase(".jpg") || format.equalsIgnoreCase(".gif")) {
            massageType = Massage.MassageType.IMAGE;
        } else {
            massageType = Massage.MassageType.FILE;
        }
        Massage massage = new Massage(chat, client.getClientProfile(), new Date(), selectedFile.getPath(), massageType);
        chat.getMassages().add(massage);
        addMassageToChat(massage);
        assert bytes != null;
        System.out.println("File length = " + selectedFile.length() + " and bytes length = " + bytes.length);
        client.connection.sendPacket(new Packet(massage, bytes, selectedFile.getName(), client.getClientProfile(), chat.getMembers().get(1), Packet.PacketPropose.MASSAGE));
        client.sendProfileToServer();

    }
    private void sendTextMassage() {
        String text = chatController.textField.getText();
        if (text.isBlank() || text.isEmpty()) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!text.isBlank()) {
            chatController.textField.clear();
            //Creat massage, add to chats array list, add to massagesBox, send massage to server, send profile to server
            Massage massage = new Massage(chat, client.getClientProfile(), new Date(), text, Massage.MassageType.TEXT);
            if (massage.getContent().isBlank() || massage.getContent().isEmpty()) {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            chat.getMassages().add(massage);
            addMassageToChat(massage);
            client.connection.sendPacket(new Packet(massage, client.getClientProfile(), chat.getMembers().get(1), Packet.PacketPropose.MASSAGE));
            client.sendProfileToServer();
        }
    }

    public void sendFriendRequestMassage() {
        String string = "Add friend request from " + chat.getMembers().get(0) + " to " + chat.getMembers().get(1);
        //Creat massage, add to chats array list, add to massagesBox, send massage to server, send profile to server
        Massage massage = new Massage(chat, client.getClientProfile(), new Date(), string, Massage.MassageType.FRIEND_REQUEST);
        chat.getMassages().add(massage);
        addMassageToChat(massage);
        client.connection.sendPacket(new Packet(massage, client.getClientProfile(), chat.getMembers().get(1), Packet.PacketPropose.MASSAGE));
        client.sendProfileToServer();
    }

    public void sendPlayRequestMassage() {
        String string = "Play together request from " + chat.getMembers().get(0) + " to " + chat.getMembers().get(1);
        Massage massage = new Massage(chat, client.getClientProfile(), new Date(), string, Massage.MassageType.PLAY_REQUEST);
        chat.getMassages().add(massage);
        addMassageToChat(massage);
        client.connection.sendPacket(new Packet(massage, client.getClientProfile(), chat.getMembers().get(1), Packet.PacketPropose.MASSAGE));
        client.sendProfileToServer();
    }

    //Receive methods
    public void receiveMassage(Packet packet) {
        if (packet.getFile() != null) {
            try {
                File file = new File(client.downloadPath + "/" + packet.getFileName());
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(packet.getFile());


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Assign massage, add to chats array list, add to massagesBox, send profile to server
        Massage massage = (Massage) packet.getContent();
        chat.getMassages().add(massage);
        addMassageToChat(massage);
        client.sendProfileToServer();
    }


    public void addMassageToChat(Massage massage) {
        if (massage.getContent().isBlank() && massage.getContent().isEmpty()) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MassageSkin massageSkin = null;
        if (massage.getMassageType().equals(Massage.MassageType.TEXT)) {
            massageSkin = new TextMassageSkin(massage, client);
        } else if (massage.getMassageType().equals(Massage.MassageType.IMAGE) || massage.getMassageType().equals(Massage.MassageType.FILE)) {
            massageSkin = new ImageMassageSkin(massage, client);
        } else if (massage.getMassageType().equals(Massage.MassageType.PLAY_REQUEST) || massage.getMassageType().equals(Massage.MassageType.FRIEND_REQUEST)) {
            massageSkin = new RequestMassageSkin(massage, client);
        } else {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MassageSkin finalMassageSkin = massageSkin;
        Platform.runLater(() -> getMassages().getChildren().add(finalMassageSkin));

        if (client.game.getGameController().chats.getSelectionModel().getSelectedItem() != null) {
            if (!client.game.getGameController().chats.getSelectionModel().getSelectedItem().equals(thisChatTab)) {
                addUnReadMassages();
            }
            getChatController().scrollPane.setVvalue(1.0);
        }


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

    public Client getClient() {
        return client;
    }

    public ChatController getChatController() {
        return chatController;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }


}
