package serlizables;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Massage implements Serializable {
    private final Chat chat;
    private final ClientProfile sender;
    private final Date date;
    private String content;
    private final MassageType massageType;
    private boolean isFinished = false;

    public void setContent(String content) {
        this.content = content;
    }

    public Massage(Chat chat, ClientProfile sender, Date date, String content, MassageType massageType) {
        this.chat = chat;
        this.sender = sender;
        this.date = date;
        this.content = content;
        this.massageType = massageType;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public MassageType getMassageType() {
        return massageType;
    }

    public Chat getChat() {
        return chat;
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

    public enum MassageType implements Serializable {
        TEXT, IMAGE, FILE, FRIEND_REQUEST, PLAY_REQUEST
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
