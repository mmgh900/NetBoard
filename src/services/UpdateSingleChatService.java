package services;

import Serlizables.Massage;
import gui.elements.ChatTab;
import gui.elements.TextMassageSkin;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import users.Client;

import java.util.ArrayList;

public class UpdateSingleChatService extends Service<ArrayList<TextMassageSkin>> {
    private final VBox massagesContainer;
    private final ArrayList<Massage> massages;
    private final ArrayList<TextMassageSkin> addedMassageBoxes = new ArrayList<>();
    private final Client client;
    private final ChatTab chatTab;

    public UpdateSingleChatService(ChatTab chatTab, ArrayList<Massage> massages, Client client) {
        this.chatTab = chatTab;
        this.client = client;
        this.massagesContainer = chatTab.getMassages();
        this.massages = massages;
        setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                for (TextMassageSkin textMassageSkin : addedMassageBoxes) {
                    massagesContainer.getChildren().add(textMassageSkin);
                    if (chatTab.getTabPane() != null && !chatTab.getTabPane().getSelectionModel().getSelectedItem().equals(chatTab)) {
                        chatTab.addUnReadMassages();
                    }
                    chatTab.getChatBoxController().scrollPane.setVvalue(1.0);
                }
            }
        });
    }

    @Override
    protected Task<ArrayList<TextMassageSkin>> createTask() {
        Task<ArrayList<TextMassageSkin>> task = new Task<ArrayList<TextMassageSkin>>() {
            @Override
            protected ArrayList<TextMassageSkin> call() throws Exception {
                for (Massage massage : massages) {
                    boolean exists = false;
                    if (chatTab.getLastUpdate().after(massage.getDate())) {
                        System.out.println("\t" + client.getClientProfile().toString() + ": " + "massage already existed. " + chatTab.getLastUpdate().toString() + " is after" + massage.getDate().toString());
                        exists = true;
                    }
                    if (!exists) {
                        boolean isSelf = (massage.getSender().equals(client.getClientProfile()));
                        addedMassageBoxes.add(new TextMassageSkin(isSelf, massage));
                        chatTab.setLastUpdate(massage.getDate());
                    }

                }
                return addedMassageBoxes;
            }
        };
        return task;
    }
}
