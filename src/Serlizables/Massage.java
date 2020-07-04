package Serlizables;

import java.io.Serializable;
import java.util.Date;

public class Massage implements Serializable {
    private ClientProfile sender;
    private Date date;
    private String content;

    public Massage() {
    }

    public Massage(ClientProfile sender, Date date, String content) {
        this.sender = sender;
        this.date = date;
        this.content = content;
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
