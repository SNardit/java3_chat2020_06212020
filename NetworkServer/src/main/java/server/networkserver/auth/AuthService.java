package server.networkserver.auth;

import java.sql.SQLException;

public interface AuthService {

    String getNickByLoginAndPassword(String login, String password);
    boolean registration (String login, String password, String nickname);
    boolean changeNickname (String oldNickname, String newNickname);

}
