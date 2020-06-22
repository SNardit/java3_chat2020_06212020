package client.controller;

import client.Command;
import client.model.NetworkService;
import client.view.auth.AuthDialogAction;
import client.view.chat.ClientChatAction;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class ClientController {
    public static final String ALL_USERS_LIST_ITEM = "All";
    private final NetworkService networkService;
    private final AuthDialogAction authDialogAction;
    private final ClientChatAction clientChatAction;
    private String nickname;


    public ClientController(String serverHost, int serverPort) {
        this.networkService = new NetworkService(serverHost, serverPort, this);
        this.authDialogAction = new AuthDialogAction(this);
        this.clientChatAction = new ClientChatAction(this);
    }

    public void runApplication() throws IOException {
        connectToServer();
        runAuthProcess();
    }

    private void runAuthProcess() {
        networkService.setSuccessfulAuthEvent(nickname -> {
            ClientController.this.setUserName(nickname);
            ClientController.this.openChat();
        });
        authDialogAction.setVisible(true);
    }

    private void openChat() {
        authDialogAction.dispose();
        networkService.setMessageHandler(clientChatAction::appendMessage);
        clientChatAction.setVisible(true);
    }

    private void setUserName(String nickname) {
        SwingUtilities.invokeLater(() -> clientChatAction.setTitle(nickname));
        this.nickname = nickname;
    }

    private void connectToServer() throws IOException {
        try {
            networkService.connect();
        } catch (IOException e) {
            System.err.println("Failed to establish server connection");
           throw e;
        }
    }

    public void sendAuthMessage(String login, String pass) {
        sendCommand(Command.authCommand(login, pass));
    }

    public void sendMessage(String message) {
        sendCommand(Command.broadcastMessageCommand(message));
    }

    private void sendCommand (Command command) {
        try {
            networkService.sendCommand(command);
        } catch (IOException e) {
            showErrorMessage(e.getMessage());
        }
    }

    public void shutdown() {
        networkService.close();
    }

    public String getUsername() {
        return nickname;
    }

    public void showErrorMessage(String errorMessage) {
        if (clientChatAction.isActive()) {
            clientChatAction.showError(errorMessage);
        }
        else if (authDialogAction.isActive()) {
            authDialogAction.showError(errorMessage);
        }
        else {
            System.err.println(errorMessage);
        }

    }


    public void sendPrivateMessage(String nickname, String message) {
        sendCommand(Command.privateMessageCommand(nickname, message));
    }

    public void updateUsersList(List<String> users) {
        users.remove(nickname);
        users.add(0, ALL_USERS_LIST_ITEM);
        clientChatAction.updateUsers(users);
    }

    public void closeAllWindows() {
        authDialogAction.dispose();
        clientChatAction.dispose();
    }
}
