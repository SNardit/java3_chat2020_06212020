package server.networkserver.auth;

import java.sql.SQLException;

public interface AuthService {

    String getNickByLoginAndPassword(String login, String password) throws SQLException;

    void start() throws ClassNotFoundException, SQLException;
    void stop();
}
