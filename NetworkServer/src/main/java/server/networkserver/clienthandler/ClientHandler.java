package server.networkserver.clienthandler;

import client.Command;
import client.command.*;
import server.networkserver.MyServer;
import server.networkserver.auth.AuthService;
import server.networkserver.sqlhandler.SQLHandler;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler {

    private final MyServer serverInstance;
    private final Socket clientSocket;
    private final AuthService authService;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    /*private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;*/
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
                    addMessageToDatabase("forAll", data.getMessage());
                    //addMessageToLocalFile("forAll", data.getMessage());
                    break;
                case PRIVATE_MESSAGE:
                    PrivateMessageCommand privateMessageCommand = (PrivateMessageCommand) command.getData();
                    String receiver = privateMessageCommand.getReceiver();
                    String message = privateMessageCommand.getMessage();
                    serverInstance.privateMessage(receiver, Command.messageCommand(nickname, message));
                    addMessageToDatabase(receiver, message);
                    break;
                case CHANGE_NICKNAME:
                    ChangeNicknameCommand changeNicknameCommand = (ChangeNicknameCommand) command.getData();
                    String newNickname = changeNicknameCommand.getNewNickname();
                    changeNickname(nickname, newNickname);
                    break;
                default:
                    String errorMessage = "Unknown type of command: " + command.getType();
                    System.err.println(errorMessage);
                    sendMessage(Command.errorCommand(errorMessage));
            }
        }
    }

   /* private void addMessageToLocalFile(String receiver, String message) {
        File file = new File("../NetworkClient/history/history_" + receiver +".txt");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
               *//* try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, true)){
                    osw.write
                }*//*

            } catch (IOException e) {
                e.printStackTrace();
            }


    }*/

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
                case SIGN_UP: {
                    SignUpCommand signUpCommand = (SignUpCommand) command.getData();
                    String login = signUpCommand.getLogin();
                    String pass = signUpCommand.getPassword();
                    String nickname = signUpCommand.getUsername();
                    addUserToDatabase(login, pass, nickname);
                    break;
                }
                case AUTH: {
                        if (processAuthCommand(command)) {
                            closeConnection.interrupt();
                            return;
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

    private void addUserToDatabase(String login, String pass, String nickname) {
        if (login.isEmpty() || login.contains(" ") || pass.isEmpty() || pass.contains(" ")) {
            try {
                sendMessage(Command.authErrorCommand("Login and/or password can not contain spaces or be empty!"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (serverInstance.getAuthService().registration(login, pass, nickname)) {
            try {
                sendMessage(Command.authErrorCommand("Your have successfully registered! And now you can sign in!"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                sendMessage(Command.authErrorCommand("Login \"" + login + "\" already exists. Choose another!"));
            } catch (IOException e) {
                e.printStackTrace();
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

    private boolean processAuthCommand(Command command) throws IOException {
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
            serverInstance.privateMessage(nickname, Command.messageCommand(null, SQLHandler.getMessagesForNick(nickname)));
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


    public void changeNickname(String nickname, String newNickname) {
        if (newNickname.isEmpty() || newNickname.contains(" ")) {
            try {
                serverInstance.privateMessage(nickname, Command.messageCommand(null, "Nickname can not contain spaces or be empty!"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (serverInstance.getAuthService().changeNickname(nickname, newNickname)) {
            try {
                serverInstance.privateMessage(nickname, Command.messageCommand(null, "Your nickname was successfully changed to " + newNickname));
                setNickname(newNickname);
                List<String> users = serverInstance.getAllUsername();
                serverInstance.broadcastMessage(Command.updateUsersListCommand(users));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                    serverInstance.privateMessage(nickname, Command.messageCommand(null, "Nickname \"" + newNickname + "\" already exists. Choose another!"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
    public void addMessageToDatabase(String receiver, String message) {
        SQLHandler.addMessages(nickname, receiver, message);
    }

}
