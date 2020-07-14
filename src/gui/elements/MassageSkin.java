package gui.elements;

import Serlizables.Massage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import users.Client;

public abstract class MassageSkin extends StackPane {
    protected Massage massage;
    protected Client client;
    protected boolean isSelf;

    private MassageSkin() {
    }

    public MassageSkin(Massage massage, Client client) {
        this.massage = massage;
        this.client = client;
        this.isSelf = (massage.getSender().equals(client.getClientProfile()));

        this.setPadding(new Insets(5, 15, 5, 15));
        this.setPrefWidth(395);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    public Massage getMassage() {
        return massage;
    }

    public Client getClient() {
        return client;
    }

    public boolean isSelf() {
        return isSelf;
    }
}
