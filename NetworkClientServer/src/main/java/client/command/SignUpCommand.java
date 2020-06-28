package client.command;

import java.io.Serializable;

public class SignUpCommand implements Serializable {

    private String login;
    private String password;
    private String username;

    public SignUpCommand(String login, String password) {
        this.login = login;
        this.password = password;
        this.username = login;
    }
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
