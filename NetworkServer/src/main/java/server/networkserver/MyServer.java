package server.networkserver;

import client.Command;
import server.networkserver.auth.AuthService;
import server.networkserver.auth.DBBaseAuthService;
import server.networkserver.clienthandler.ClientHandler;
import server.networkserver.sqlhandler.SQLHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyServer {

    private int port;
    private List<ClientHandler> clients;
    private AuthService authService;
    private  ExecutorService service;
    private static final Logger logger = Logger.getLogger(MyServer.class.getName());


    public MyServer(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.authService = new DBBaseAuthService();
        this.service = Executors.newCachedThreadPool();
    }

    public void start () {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.log(Level.INFO, "Server is running");
            //System.out.println("Server is running");
            if (!SQLHandler.connect()) {
                throw new RuntimeException("Problems with connecting to the database");
            }
            //authService.start();
            //noinspection InfiniteLoopStatement
            while (true) {
                logger.log(Level.INFO, "Server is waiting connection....");
                //System.out.println("Server is waiting connection....");
                Socket clientSocket = serverSocket.accept();
                logger.log(Level.INFO, "Client has been connected");
                //System.out.println("Client has been connected");
                ClientHandler handler = new ClientHandler(clientSocket, this);
                try {
                    handler.handle();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Filed to handle client connection!");
                    //System.err.println("Filed to handle client connection!");
                    clientSocket.close();
                    service.shutdown();
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            //System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            SQLHandler.disconnect();
        }
    }

    public void serviceExecute(Runnable task){
        service.execute(task);
    }
    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNicknameBusy(String nickname) {
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(Command command) throws IOException {
        for (ClientHandler client : clients) {
            client.sendMessage(command);
        }
    }


    public synchronized void privateMessage(String receiver, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(receiver)) {
            client.sendMessage(command);
            return;
            }
        }
    }


    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        List<String> users = getAllUsername();
        broadcastMessage(Command.updateUsersListCommand(users));
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        List<String> users = getAllUsername();
        broadcastMessage(Command.updateUsersListCommand(users));
    }

    public List<String> getAllUsername() {
        /*return clients.stream()
                .map(ClientHandler::getNickname)
                .collect(Collectors.toList());*/

        List<String> result = new ArrayList<>();
        for (ClientHandler client : clients) {
            result.add(client.getNickname());
        }
        return result;
    }


}
