package Serlizables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Chat implements Serializable {
    protected String name;
    protected ArrayList<Massage> massages;
    protected ArrayList<ClientProfile> members;


    public Chat(ClientProfile member1, ClientProfile member2) {
        this.massages = new ArrayList<>();
        this.members = new ArrayList<>();
        members.add(member1);
        members.add(member2);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Chat))
            return false;
        Chat chat = (Chat) o;
        return getMembers().equals(chat.getMembers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMembers());
    }

    public ArrayList<Massage> getMassages() {
        return massages;
    }

    public void setMassages(ArrayList<Massage> massages) {
        this.massages = massages;
    }

    public ArrayList<ClientProfile> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<ClientProfile> members) {
        this.members = members;
    }

    public void addMassage(String text, ClientProfile sender) {
        massages.add(new Massage(sender, new Date(), text));
    }
}
