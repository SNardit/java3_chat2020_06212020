package server.networkserver.clienthandler;

import client.Command;
import client.command.AuthCommand;
import client.command.BroadcastMessageCommand;
import client.command.PrivateMessageCommand;
import server.networkserver.MyServer;
import server.networkserver.auth.AuthService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {

    private final MyServer serverInstance;
    private final Socket clientSocket;
    private final AuthService authService;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String nickname;

    public ClientHandler(Socket clientSocket, MyServer myServer) {
        this.clientSocket = clientSocket;
        this.serverInstance = myServer;
        this.authService = serverInstance.getAuthService();

    }

    public void handle() throws IOException {
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        new Thread(()-> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                System.out.println("Connection has been failed!");
            } finally {
                closeConnection();
            }
        }).start();
    }

    private void closeConnection() {
        try {
            serverInstance.unsubscribe(this);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            switch (command.getType()) {
                case END:
                    return;
                case BROADCAST_MESSAGE:
                    BroadcastMessageCommand data = (BroadcastMessageCommand) command.getData();
                    serverInstance.broadcastMessage(Command.messageCommand(nickname, data.getMessage()));
                    break;
                case PRIVATE_MESSAGE:
                    PrivateMessageCommand privateMessageCommand = (PrivateMessageCommand) command.getData();
                    String receiver = privateMessageCommand.getReceiver();
                    String message = privateMessageCommand.getMessage();
                    serverInstance.privateMessage(receiver, Command.messageCommand(nickname, message));
                    break;
                default:
                    String errorMessage = "Unknown type of command: " + command.getType();
                    System.err.println(errorMessage);
                    sendMessage(Command.errorCommand(errorMessage));
            }
        }
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown type of object from client!";
            System.err.println(errorMessage);
            e.printStackTrace();
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }

    private void authentication() throws IOException {

        Thread closeConnection = getTimeThreadForCloseConnection();

        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }
            switch (command.getType()) {
                case AUTH: {
                    try {
                        if (processAuthCommand(command)) {
                            closeConnection.interrupt();
                            return;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default:
                    String errorMessage = "Illegal command for authentication: " + command.getType();
                    System.err.println(errorMessage);
                    sendMessage(Command.errorCommand(errorMessage));
            }
        }
    }

    private Thread getTimeThreadForCloseConnection() {
        Thread closeConnection = new Thread(() -> {
            try {
                Thread.sleep(120000);
                if (!Thread.currentThread().isInterrupted()) {
                    clientSocket.close();
                }
            } catch (InterruptedException e) {
                System.out.println("Authentication has been successful");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        closeConnection.start();
        return closeConnection;
    }

    private boolean processAuthCommand(Command command) throws IOException, SQLException {
        AuthCommand authCommand = (AuthCommand) command.getData();
        String login = authCommand.getLogin();
        String password = authCommand.getPassword();
        String nickname = authService.getNickByLoginAndPassword(login, password);
        if (nickname == null) {
            sendMessage(Command.authErrorCommand("Wrong login or/and password!"));
        }
        else if (serverInstance.isNicknameBusy(nickname)) {
            sendMessage(Command.authErrorCommand("Account is already using!"));
        }
        else {
            authCommand.setUsername(nickname);
            sendMessage(command);
            setNickname(nickname);
            serverInstance.broadcastMessage(Command.messageCommand(null, nickname + " is online!"));
            serverInstance.subscribe(this);
            return true;
        }
        return false;
    }

    private void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void sendMessage(Command command) throws IOException {
        outputStream.writeObject(command);
    }
}
