package serlizables;

import users.Client;

import java.io.Serializable;

public class Packet implements Serializable {
    private Serializable[] contents = new Serializable[1];
    private final PacketPropose propose;
    private ClientProfile senderProfile;
    private String fileName;
    private byte[] file;

    private ClientProfile receiverProfile;


    public Packet(Massage massage, byte[] file, String fileName, ClientProfile senderProfile, ClientProfile receiverProfile, PacketPropose propose) {
        this.file = file;
        this.fileName = fileName;

        this.contents[0] = massage;
        this.propose = propose;

        this.senderProfile = senderProfile;
        this.receiverProfile = receiverProfile;
    }

    public Packet(Serializable content, ClientProfile sender, ClientProfile receiver, PacketPropose propose) {
        this.contents[0] = content;
        this.senderProfile = sender;
        this.receiverProfile = receiver;
        this.propose = propose;
    }

    public Packet(Serializable content, ClientProfile sender, PacketPropose propose) {
        this.contents[0] = content;
        this.senderProfile = sender;
        this.propose = propose;
    }

    public Packet(Serializable content, Client sender, PacketPropose propose) {
        this.contents[0] = content;
        this.senderProfile = sender.getClientProfile();
        this.propose = propose;
    }

    public Packet(Serializable content, PacketPropose propose) {

        this.contents[0] = content;
        this.propose = propose;
    }

    public Packet(Serializable content, Serializable content2, PacketPropose propose) {
        this.contents = new Serializable[2];
        this.contents[0] = content;
        this.contents[1] = content2;
        this.propose = propose;
    }


    public Serializable getContent() {
        return contents[0];
    }

    public Serializable[] getContents() {
        return contents;
    }


    public PacketPropose getPropose() {
        return propose;
    }

    public String getFileName() {
        return fileName;
    }

    public ClientProfile getSenderProfile() {
        return senderProfile;
    }

    public ClientProfile getReceiverProfile() {
        return receiverProfile;
    }

    public byte[] getFile() {
        return file;
    }

    public enum PacketPropose {
        LOAD_PACKET, MASSAGE, LOGIN_REQUEST, LOGOUT_REQUEST, PROFILES_IN_SYSTEM, PROFILE_INFO, RECOVER_PASSWORD_REQUEST, RESPOND_ADD_FRIEND, RESPOND_PLAY_TOGETHER, SERVER_ERROR_IN_LOGIN, SERVER_RESPOND_TO_RECOVER_PASS, SERVER_RESPOND_TO_SIGNUP, SIGN_UP_REQUEST, START_GAME, UPDATE_GAME, SEARCH_USERNAME;

        @Override
        public String toString() {
            String s = super.toString();
            s = s.toLowerCase();
            s = s.replace('_', ' ');
            return s;
        }
    }
}
