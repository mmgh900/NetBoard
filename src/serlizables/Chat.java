package serlizables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Chat implements Serializable {
    protected String name;
    protected ArrayList<Massage> massages;
    protected ArrayList<ClientProfile> members;
    protected Date lastMassage;


    public Chat(ClientProfile member1, ClientProfile member2) {
        this.massages = new ArrayList<>() {
            @Override
            public boolean add(Massage massage) {
                boolean result = super.add(massage);
                System.out.println("something added:" + massage.getContent());
                //lastMassage = massage.getDate();
                return result;
            }
        };
        System.out.println("nothing has added:" + massages.size());
        this.members = new ArrayList<>();
        this.lastMassage = new Date();
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


    public ArrayList<Massage> getMassages() {
        return massages;
    }

    public ArrayList<ClientProfile> getMembers() {
        return members;
    }

    public Date getLastMassage() {
        return lastMassage;
    }

}
