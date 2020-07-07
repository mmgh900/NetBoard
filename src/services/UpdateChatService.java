package services;

import Serlizables.Chat;
import gui.elements.ChatTab;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import users.Client;

import java.util.ArrayList;

public class UpdateChatService extends Service<ArrayList<ChatTab>> {
    private final TabPane chats;
    private final Client client;
    private final ArrayList<ChatTab> addedTabs = new ArrayList<>();

    public UpdateChatService(TabPane chats, Client client) {
        this.chats = chats;
        this.client = client;

        setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                for (ChatTab chatTab : addedTabs) {
                    chats.getTabs().add(chatTab);
                }

            }
        });
    }

    @Override
    protected Task<ArrayList<ChatTab>> createTask() {
        Task<ArrayList<ChatTab>> task = new Task<ArrayList<ChatTab>>() {
            @Override
            protected ArrayList<ChatTab> call() throws Exception {
                for (Chat chat : client.getClientProfile().getChats()) {
                    ChatTab foundTab = null;
                    for (Tab tab : chats.getTabs()) {
                        if (tab instanceof ChatTab && ((ChatTab) tab).getChat().equals(chat)) {
                            foundTab = (ChatTab) tab;
                        }
                    }
                    if (foundTab == null) {
                        foundTab = new ChatTab(chat, client);
                        addedTabs.add(foundTab);
                        foundTab.refreshMassages(chat);
                    } else if (foundTab.getLastUpdate().before(chat.getLastMassage())) {

                        foundTab.refreshMassages(chat);
                    } else {
                        System.out.println("\t" + client.getClientProfile().toString() + ": " + "massage already existed. " + foundTab.getLastUpdate().toString() + " is before" + chat.getLastMassage().toString());

                    }

                }
                return addedTabs;
            }
        };

        return task;
    }
}


                /*chats.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
                        if (tab != null && t1 != null) {
                            //System.out.println("Changed from" + tab.getText() + " to " + t1.getText());
                            makeItZaro(t1);
                        }

                    }
                });*/
