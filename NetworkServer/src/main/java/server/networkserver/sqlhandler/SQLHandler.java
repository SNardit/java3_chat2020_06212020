package server.networkserver.sqlhandler;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement psGetNickname;
    private static PreparedStatement psRegistration;
    private static PreparedStatement psChangeNick;

    private static PreparedStatement psAddMessage;
    private static PreparedStatement psGetMessageForNick;

    private static final Logger logger = Logger.getLogger(SQLHandler.class.getName());


    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            prepareAllStatements();
            return true;
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void disconnect() {
        try {
            psGetNickname.close();
            psRegistration.close();
            psChangeNick.close();

            psAddMessage.close();
            psGetMessageForNick.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

    }


    private static void prepareAllStatements() throws SQLException {
        psRegistration = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?, ?, ?);");
        psGetNickname = connection.prepareStatement("SELECT nickname FROM users WHERE login = ? AND password = ?;");
        psChangeNick = connection.prepareStatement("UPDATE users SET nickname = ? WHERE nickname = ?;");

        psAddMessage = connection.prepareStatement("INSERT INTO messages (sender, receiver, text, date) " +
                "VALUES ((SELECT id FROM users WHERE nickname = ?), (SELECT id FROM users WHERE nickname = ?), ?, DATETIME('NOW'));");

        psGetMessageForNick = connection.prepareStatement("SELECT \n" +
                "(SELECT nickname FROM users WHERE id = sender),\n" +
                "(SELECT nickname FROM users WHERE id = receiver),\n" +
                "text,\n" +
                "date\n" +
                "FROM messages\n" +
                "WHERE sender = (SELECT id FROM users WHERE nickname = ?)\n " +
                "OR receiver = (SELECT id FROM users WHERE nickname = ?)\n " +
                "OR receiver = (SELECT id FROM users WHERE nickname = 'forAll');");
                         /*"SELECT * FROM ("
                        + "SELECT (SELECT nickname FROM users WHERE id = sender),\n" +
                        "(SELECT nickname FROM users WHERE id = receiver),\n" +
                        "text,\n" +
                        "date\n" +
                        "FROM messages\n" +
                        "WHERE sender = (SELECT id FROM users WHERE nickname = ?)\n " +
                        "OR receiver = (SELECT id FROM users WHERE nickname = ?)\n " +
                        "OR receiver = (SELECT id FROM users WHERE nickname = 'forAll') ORDER BY date DESC LIMIT 100)" +
                        "ORDER BY date;");*/

    }

    public static String getNickByLoginAndPassword(String login, String password) {
        String nickname = null;
        try {
            psGetNickname.setString(1, login);
            psGetNickname.setString(2, password);
            ResultSet rs = psGetNickname.executeQuery();
            if(rs.next()) {
                nickname = rs.getString("nickname");
            }
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return nickname;
    }

    public static boolean registration (String login, String password, String nickname) {
        try {
            psRegistration.setString(1, login);
            psRegistration.setString(2, password);
            psRegistration.setString(3, nickname);
            psRegistration.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean changeNickname (String oldName, String newName) {
        try {
            psChangeNick.setString(1, newName);
            psChangeNick.setString(2, oldName);
            psChangeNick.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean addMessages (String sender, String receiver, String text) {
        try {
            psAddMessage.setString(1, sender);
            psAddMessage.setString(2, receiver);
            psAddMessage.setString(3, text);
            psAddMessage.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public static String getMessagesForNick(String nick) {
        StringBuilder sb = new StringBuilder();
        try {
            psGetMessageForNick.setString(1, nick);
            psGetMessageForNick.setString(2, nick);
            ResultSet rs = psGetMessageForNick.executeQuery();

            while (rs.next()) {
                String sender = rs.getString(1);
                String receiver = rs.getString(2);
                String text = rs.getString(3);
                String date = rs.getString(4);

                if (receiver.equals("forAll")) {
                    sb.append(String.format("(%s) %s : %s\n", date, sender, text));
                } else {
                    sb.append(String.format("(%s) [%s] to [%s] : %s\n", date, sender, receiver, text));
                }
            }
            rs.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
        return sb.toString();

    }

}
