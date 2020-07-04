package Serlizables;

import java.io.Serializable;
import java.util.Objects;

public class UserInfo implements Serializable {
    protected String username;
    protected String password;

    public UserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserInfo() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserInfo))
            return false;
        UserInfo userInfo = (UserInfo) o;
        return getUsername().toLowerCase().equals(userInfo.getUsername().toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername().toLowerCase());
    }

    @Override
    public String toString() {
        return "[" + username.toUpperCase() + "]";
    }

}
