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
        name = member2.getFirstName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Chat))
            return false;
        Chat chat = (Chat) o;
        boolean allMembersAreTheSame = true;
        for (int i = 0; i < chat.members.size(); i++) {
            if (!chat.members.get(i).equals(this.members.get(i))) {
                allMembersAreTheSame = false;
            }
        }
        return getName().equals(chat.getName()) && allMembersAreTheSame;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getMembers());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
