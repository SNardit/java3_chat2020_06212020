package server.networkserver;

import client.Command;
import server.networkserver.auth.AuthService;
import server.networkserver.auth.BaseAuthService;
import server.networkserver.clienthandler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private int port;
    private List<ClientHandler> clients;
    private AuthService authService;

    public MyServer(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.authService = new BaseAuthService();
    }

    public void start () {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running");
            //authService.start();
            //noinspection InfiniteLoopStatement
            while (true) {
                System.out.println("Server is waiting connection....");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client has been connected");
                ClientHandler handler = new ClientHandler(clientSocket, this);
                try {
                    handler.handle();
                } catch (IOException e) {
                    System.err.println("Filed to handle client connection!");
                    clientSocket.close();
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            authService.stop();
        }
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

    private List<String> getAllUsername() {
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
