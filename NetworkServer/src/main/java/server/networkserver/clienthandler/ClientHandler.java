package server.networkserver.clienthandler;

import client.Command;
import client.command.*;
import server.networkserver.MyServer;
import server.networkserver.auth.AuthService;
import server.networkserver.sqlhandler.SQLHandler;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {

    private final MyServer serverInstance;
    private final Socket clientSocket;
    private final AuthService authService;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private String nickname;

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());


    public ClientHandler(Socket clientSocket, MyServer myServer) {
        this.clientSocket = clientSocket;
        this.serverInstance = myServer;
        this.authService = serverInstance.getAuthService();

    }

    public void handle() throws IOException {
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        serverInstance.serviceExecute(new Runnable() {
            @Override
            public void run() {
                try {
                    authentication();
                    readMessage();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Connection has been failed!");
                    //System.out.println("Connection has been failed!");
                } finally {
                    closeConnection();
                }
            }
        });
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
                    logger.log(Level.INFO, "END");
                    return;
                case BROADCAST_MESSAGE:
                    BroadcastMessageCommand data = (BroadcastMessageCommand) command.getData();
                    serverInstance.broadcastMessage(Command.messageCommand(nickname, data.getMessage()));
                    addMessageToDatabase("forAll", data.getMessage());
                    logger.log(Level.INFO, nickname +" has sent broadcast_message");
                   // addMessageToLocalFile("All", data.getMessage());
                    break;
                case PRIVATE_MESSAGE:
                    PrivateMessageCommand privateMessageCommand = (PrivateMessageCommand) command.getData();
                    String receiver = privateMessageCommand.getReceiver();
                    String message = privateMessageCommand.getMessage();
                    serverInstance.privateMessage(receiver, Command.messageCommand(nickname, message));
                    addMessageToDatabase(receiver, message);
                    logger.log(Level.INFO, nickname +" has sent message to " + receiver);
                    //addMessageToLocalFile(receiver, message);
                    break;
                case CHANGE_NICKNAME:
                    ChangeNicknameCommand changeNicknameCommand = (ChangeNicknameCommand) command.getData();
                    String newNickname = changeNicknameCommand.getNewNickname();
                    changeNickname(nickname, newNickname);
                    logger.log(Level.INFO, nickname +" has change nickname to " + newNickname);
                    break;
                default:
                    String errorMessage = "Unknown type of command: " + command.getType();
                    logger.log(Level.SEVERE, errorMessage);
                    //System.err.println(errorMessage);
                    sendMessage(Command.errorCommand(errorMessage));
            }
        }
    }

   /* private void addMessageToLocalFile(String receiver, String message) {
        File fileSender = new File(("./NetworkClient/history/history_" + nickname + ".txt"));
        ifFileExist(fileSender);
        addToFile(fileSender, receiver, message);

        File fileReceiver = new File(("./NetworkClient/history/history_" + receiver + ".txt"));
        ifFileExist(fileReceiver);
        addToFile(fileReceiver, receiver, message);

    }


    private void ifFileExist (File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }

    private void addToFile (File file, String receiver, String message) {
        //LocalDateTime date = LocalDateTime.now();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file, true));) {
            bf.write("\n(" + date + ") " + nickname + " to " + receiver + ": " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    private Command readCommand() throws IOException {
        try {
            return (Command) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown type of object from client!";
            logger.log(Level.SEVERE, errorMessage);
            //System.err.println(errorMessage);
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
                    logger.log(Level.INFO, "New registration: login - "+ login);
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
                    logger.log(Level.SEVERE, errorMessage);
                    //System.err.println(errorMessage);
                    sendMessage(Command.errorCommand(errorMessage));
            }
        }
    }

    private void addUserToDatabase(String login, String pass, String nickname) throws IOException {
        if (login.isEmpty() || login.contains(" ") || pass.isEmpty() || pass.contains(" ")) {
                sendMessage(Command.authErrorCommand("Login and/or password can not contain spaces or be empty!"));
        }
        else if (serverInstance.getAuthService().registration(login, pass, nickname)) {
                sendMessage(Command.authErrorCommand("Your have successfully registered! And now you can sign in!"));
        }
        else {
                sendMessage(Command.authErrorCommand("Login \"" + login + "\" already exists. Choose another!"));
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
                logger.log(Level.INFO, "Authentication "+ nickname + " has been successful!");
                //System.out.println("Authentication has been successful");
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
            //serverInstance.privateMessage(nickname, Command.messageCommand(null, SQLHandler.getMessagesForNick(nickname)));
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


    public void changeNickname(String nickname, String newNickname) throws IOException{
        if (newNickname.isEmpty() || newNickname.contains(" ")) {
                serverInstance.privateMessage(nickname, Command.messageCommand(null, "Nickname can not contain spaces or be empty!"));
        }
        else if (serverInstance.getAuthService().changeNickname(nickname, newNickname)) {
                serverInstance.privateMessage(nickname, Command.messageCommand(null, "Your nickname was successfully changed to " + newNickname));
                setNickname(newNickname);
                List<String> users = serverInstance.getAllUsername();
                serverInstance.broadcastMessage(Command.updateUsersListCommand(users));
        }
        else {
                serverInstance.privateMessage(nickname, Command.messageCommand(null, "Nickname \"" + newNickname + "\" already exists. Choose another!"));
            }
    }
    public void addMessageToDatabase(String receiver, String message) {
        SQLHandler.addMessages(nickname, receiver, message);
    }

}
