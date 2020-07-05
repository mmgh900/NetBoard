package controllers;

import users.Client;

public abstract class StandardController {
    protected Client client;

    public void setClient(Client client) {
        this.client = client;
    }
}
