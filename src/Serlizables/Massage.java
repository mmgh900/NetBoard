package Serlizables;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Massage implements Serializable {
    private final Chat chat;
    private final ClientProfile sender;
    private final Date date;
    private final String content;


    public Massage(Chat chat, ClientProfile sender, Date date, String content) {
        this.chat = chat;
        this.sender = sender;
        this.date = date;
        this.content = content;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Massage))
            return false;
        Massage massage = (Massage) o;
        return getSender().equals(massage.getSender()) && getDate().equals(massage.getDate()) && getContent().equals(massage.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender(), getDate(), getContent());
    }

    public ClientProfile getSender() {
        return sender;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }


}
