package ro.uvt.dp.services;

import ro.uvt.dp.entities.Client;

public interface Mediator {
    void sendMessage(String message, Client sender);
    void sendMessageToClient(String message, Client receiver);
    void sendMessageToAll(String message);
}
