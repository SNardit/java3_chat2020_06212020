package server.networkserver.auth;

import server.networkserver.sqlhandler.SQLHandler;

public class DBBaseAuthService implements AuthService {

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        return SQLHandler.getNickByLoginAndPassword(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return SQLHandler.registration(login, password, nickname);
    }

    @Override
    public boolean changeNickname(String oldNickname, String newNickname) {
        return SQLHandler.changeNickname(oldNickname, newNickname);
    }

}
