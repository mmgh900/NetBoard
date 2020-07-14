package Serlizables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class ClientProfile implements Serializable {



    /*    private final UserInfo userInfo;*/

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private final ArrayList<ClientProfile> friends = new ArrayList<>();
    private ArrayList<Chat> chats;
    private SecurityQuestions securityQuestion;
    private String answer;
    private Boolean isOnline;
    private GameStatistics ticTacToeStatistics;
    private final ArrayList<Packet> savedPackets = new ArrayList<>();

    public ArrayList<Packet> getSavedMassages() {
        return savedPackets;
    }

    private boolean isPlayingOnline;

    public boolean isPlayingOnline() {
        return isPlayingOnline;
    }

    public void setPlayingOnline(boolean playingOnline) {
        isPlayingOnline = playingOnline;
    }

    public ClientProfile(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public ClientProfile(String email, SecurityQuestions securityQuestion, String answer) {
        this.email = email;
        this.securityQuestion = securityQuestion;
        this.answer = answer;
    }

    public ClientProfile(String firstName, String lastName, String username, String password, SecurityQuestions securityQuestion, String answer, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.securityQuestion = securityQuestion;
        this.answer = answer;
        this.email = email;
        this.chats = new ArrayList<>();
        ticTacToeStatistics = new GameStatistics(0, 0, 0, 0);
        isOnline = false;
    }

    @Override
    public String toString() {
        if (username == null) {
            return "[GUEST]";
        } else {
            return "[" + username.toUpperCase() + "]";
        }
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


    public SecurityQuestions getSecurityQuestion() {
        return securityQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public String getEmail() {
        return email;
    }


    public ClientProfile(String username, String firstName, String lastName, String email, String answer, Boolean isOnline, GameStatistics ticTacToeStatistics, boolean isPlayingOnline) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.answer = answer;
        this.isOnline = isOnline;
        this.ticTacToeStatistics = ticTacToeStatistics;
        this.isPlayingOnline = isPlayingOnline;
    }

    public GameStatistics getTicTacToeStatistics() {
        return ticTacToeStatistics;
    }

    public ClientProfile makeSafeClone() {
        ClientProfile clone = new ClientProfile(username, firstName, lastName, email, answer, isOnline, ticTacToeStatistics, isPlayingOnline);
        return clone;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void clearSecurityInfo() {
        this.securityQuestion = null;
        this.password = null;
        this.answer = null;
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
        if (!(o instanceof ClientProfile))
            return false;
        ClientProfile that = (ClientProfile) o;
        return getUsername().equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
}
