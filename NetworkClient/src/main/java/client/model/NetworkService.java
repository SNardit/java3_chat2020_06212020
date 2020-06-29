package client.model;

import client.Command;
import client.command.*;
import client.controller.AuthEvent;
import client.controller.ClientController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class NetworkService {

    private final String host;
    private final int port;
    private ClientController controller;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Consumer<String> messageHandler;
    private AuthEvent successfulAuthEvent;

    public NetworkService(String host, int port, ClientController controller) {
        this.host = host;
        this.port = port;
        this.controller = controller;
    }


    public void connect() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 10000);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        runReadThread();
    }

    private void runReadThread() {
        new Thread(() -> {
            while (true) {
                try {
                    Command command = (Command) in.readObject();
                    processCommand(command);
                } catch (IOException e) {
                    System.out.println("Поток чтения был прерван!");
                    controller.closeAllWindows();
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void processCommand(Command command) {
        switch (command.getType()) {
            case AUTH: {
                processAuthCommand(command);
                break;
            }
            case MESSAGE: {
                processMessageCommand(command);
                break;
            }
            case AUTH_ERROR:
            case ERROR: {
                processErrorCommand(command);
                break;
            }
            case CHANGE_NICKNAME: {
                processChangeNicknameCommand(command);
                break;
            }
            case UPDATE_USERS_LIST: {
                updateUsersListCommand(command);
                break;
            }
            default:
                System.err.println("Unknown type of command " + command.getType());
        }
    }


    private void processChangeNicknameCommand(Command command) {
        ChangeNicknameCommand data = (ChangeNicknameCommand) command.getData();
        String newNickname = data.getNewNickname();
        controller.changeNickname(newNickname);
    }

    private void updateUsersListCommand(Command command) {
        UpdateUsersListCommand data = (UpdateUsersListCommand) command.getData();
        List<String> users = data.getUsers();
        controller.updateUsersList(users);
    }

    private void processErrorCommand(Command command) {
        ErrorCommand data = (ErrorCommand) command.getData();
        controller.showErrorMessage(data.getErrorMessage());
    }

    private void processMessageCommand(Command command) {
        MessageCommand data = (MessageCommand) command.getData();
        if (messageHandler != null) {
            String message = data.getMessage();
            String username = data.getUsername();
            if (username != null) {
                message = username + ": " + message;
            }
            messageHandler.accept(message);
        }
    }

    private void processAuthCommand(Command command) {
        AuthCommand data = (AuthCommand) command.getData();
        String nickname = data.getUsername();
        successfulAuthEvent.authIsSuccessful(nickname);
    }

    public void sendCommand(Command command) throws IOException {
        out.writeObject(command);
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setSuccessfulAuthEvent(AuthEvent successfulAuthEvent) {
        this.successfulAuthEvent = successfulAuthEvent;
    }

    public void close() {
        try {
            sendCommand(Command.endCommand());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
