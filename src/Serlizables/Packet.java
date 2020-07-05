package Serlizables;

import users.Client;
import users.Server;

import java.io.Serializable;

public class Packet implements Serializable {
    private final Serializable content;
    private final PacketPropose propose;
    private ClientProfile senderProfile;

    private ClientProfile receiverProfile;


    public Packet(Serializable content, ClientProfile sender, ClientProfile receiver, PacketPropose propose) {
        this.content = content;
        this.senderProfile = sender;
        this.receiverProfile = receiver;
        this.propose = propose;
    }

    public Packet(Serializable content, ClientProfile sender, PacketPropose propose) {
        this.content = content;
        this.senderProfile = sender;
        this.propose = propose;
    }

    public Packet(Serializable content, Client sender, PacketPropose propose) {
        this.content = content;
        this.senderProfile = sender.getClientProfile();
        this.propose = propose;
    }

    public Packet(Serializable content, Server sender, PacketPropose propose) {
        this.content = content;
        this.propose = propose;
    }


    public Serializable getContent() {
        return content;
    }

    public PacketPropose getPropose() {
        return propose;
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
