package Serlizables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class ClientProfile implements Serializable {

    private final UserInfo userInfo;

    private final String firstName;
    private final String lastName;
    private final String email;
    private final ArrayList<ClientProfile> friends = new ArrayList<>();
    private final ArrayList<Chat> chats;
    private SecurityQuestions securityQuestion;
    private String answer;
    private Boolean isOnline;
    private int totalOnlineWins;
    private int totalOnlineLosses;
    private int singlePlayerWins;
    private int singlePlayerLosses;


    public ClientProfile(String firstName, String lastName, String username, String password, SecurityQuestions securityQuestion, String answer, String email) {
        this.userInfo = new UserInfo(username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.securityQuestion = securityQuestion;
        this.answer = answer;
        this.email = email;
        this.chats = new ArrayList<>();
        singlePlayerLosses = 0;
        singlePlayerWins = 0;
        totalOnlineLosses = 0;
        totalOnlineWins = 0;
        isOnline = true;
    }

    @Override
    public String toString() {
        return "[" + userInfo.getUsername().toUpperCase() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ClientProfile))
            return false;
        ClientProfile that = (ClientProfile) o;
        return getUserInfo().equals(that.getUserInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserInfo());
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ArrayList<ClientProfile> getFriends() {
        return friends;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public int getTotalOnlineWins() {
        return totalOnlineWins;
    }

    public void setTotalOnlineWins(int totalOnlineWins) {
        this.totalOnlineWins = totalOnlineWins;
    }

    public int getTotalOnlineLosses() {
        return totalOnlineLosses;
    }

    public void setTotalOnlineLosses(int totalOnlineLosses) {
        this.totalOnlineLosses = totalOnlineLosses;
    }

    public int getSinglePlayerLosses() {
        return singlePlayerLosses;
    }

    public void setSinglePlayerLosses(int singlePlayerLosses) {
        this.singlePlayerLosses = singlePlayerLosses;
    }

    public SecurityQuestions getSecurityQuestion() {
        return securityQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public String getEmail() {
        return email;
    }

    public int getSinglePlayerWins() {
        return singlePlayerWins;
    }

    public void setSinglePlayerWins(int singlePlayerWins) {
        this.singlePlayerWins = singlePlayerWins;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void clearSecurityInfo() {
        this.securityQuestion = null;
        this.userInfo.password = null;
        this.answer = null;
    }
}
