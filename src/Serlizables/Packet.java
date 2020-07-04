package Serlizables;

import users.User;

import java.io.Serializable;

public class Packet implements Serializable {
    private final Serializable content;
    private final PacketPropose propose;
    private UserInfo sender;
    private ClientProfile senderProfile;
    private UserInfo receiver;
    private ClientProfile receiverProfile;

    public Packet(Serializable content, UserInfo sender, UserInfo receiver, PacketPropose propose) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.propose = propose;
    }

    public Packet(Serializable content, ClientProfile sender, ClientProfile receiver, PacketPropose propose) {
        this.content = content;
        this.senderProfile = sender;
        this.receiverProfile = receiver;
        this.propose = propose;
    }

    public Packet(Serializable content, User sender, UserInfo receiver, PacketPropose propose) {
        this.content = content;
        this.sender = sender.userInfo;
        this.receiver = receiver;
        this.propose = propose;
    }

    public Packet(Serializable content, UserInfo sender, PacketPropose propose) {
        this.content = content;
        this.sender = sender;
        this.propose = propose;
    }

    public Packet(Serializable content, User sender, PacketPropose propose) {
        this.content = content;
        this.sender = sender.userInfo;
        this.propose = propose;
    }

    public Serializable getContent() {
        return content;
    }

    public UserInfo getSender() {
        return sender;
    }

    public PacketPropose getPropose() {
        return propose;
    }

    public UserInfo getReceiver() {
        return receiver;
    }

    public ClientProfile getSenderProfile() {
        return senderProfile;
    }

    public ClientProfile getReceiverProfile() {
        return receiverProfile;
    }

    public enum PacketPropose {
        SIGN_UP_REQUEST, LOGIN_REQUEST, LOGOUT_REQUEST, SERVER_RESPOND_TO_LOGIN, SERVER_RESPOND_TO_SIGNUP, PROFILE_INFO, PROFILES_IN_SYSTEM, PLAY_TOGETHER_REQUEST, RESPOND_PLAY_TOGETHER, ADD_FRIEND_REQUEST, RESPOND_ADD_FRIEND, CANCEL_TOGETHER_REQUEST, UPDATE_GAME, START_GAME, CHAT;

        @Override
        public String toString() {
            String s = super.toString();
            s = s.toLowerCase();
            s = s.replace('_', ' ');
            return s;
        }
    }
}
