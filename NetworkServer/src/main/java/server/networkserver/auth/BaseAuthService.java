package server.networkserver.auth;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class BaseAuthService implements AuthService {

    private Connection connection;
    private Statement stmt;
    private PreparedStatement psSelect;

  /*  private static class AuthEntry {
        private String login;
        private String password;



        public AuthEntry(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AuthEntry authEntry = (AuthEntry) o;
            return Objects.equals(login, authEntry.login) &&
                    Objects.equals(password, authEntry.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(login, password);
        }
    }*/

    //public static final Map  <AuthEntry, String> NICK_BY_LOGIN_AND_PASSWORD = new HashMap<>();

    /*private static void fillMap () {
        for (int i = 0; i < 5; i++) {
            int number = i + 1;
            NICK_BY_LOGIN_AND_PASSWORD.put(new AuthEntry("login" + number, "pass" + number), "nick" + number);
        }
    }*/

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        String nickname = null;
        try {
            start();

            psSelect = connection.prepareStatement("SELECT nickname FROM users WHERE login = ? AND password = ?");
            psSelect.setString(1, login);
            psSelect.setString(2, password);
            ResultSet rs = psSelect.executeQuery();


            nickname = rs.getString("nickname");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            stop();
        }



        //fillMap();
        //return NICK_BY_LOGIN_AND_PASSWORD.get(new AuthEntry (login, password));

        return nickname;
    }

    @Override
    public void start() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
        System.out.println("Auth service has been started");

    }

    @Override
    public void stop() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Auth service has been stopped");

    }
}

