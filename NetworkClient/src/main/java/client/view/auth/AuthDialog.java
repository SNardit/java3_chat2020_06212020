package client.view.auth;

import javax.swing.*;
import java.awt.*;

public class AuthDialog {

    public static final Color LIGHT_GREEN = new Color(200, 250, 220);
    public static final Color AQUAMARINE = new Color(10, 50, 100);
    public static final Color BRIGHT_GREEN = new Color(100, 200, 170);
    public static final Color LIGHT_PINK = new Color(250, 230, 230);

    public static JPanel contentPanel;
    public static JButton signIn;
    public static JButton signUp;
    public static JButton exit;
    public static JTextField entryLogin;
    public static JPasswordField entryPass;

    static class MyWindowAuth extends JFrame {

        public MyWindowAuth() {

            contentPanel = new JPanel(new BorderLayout());
            add(contentPanel);

            JPanel authPanel = new JPanel(new BorderLayout());
            authPanel.setBackground(LIGHT_GREEN);
            contentPanel.add(authPanel);
            authPanel.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));

            JPanel loginPanel = new JPanel(new BorderLayout());
            authPanel.add(loginPanel, BorderLayout.NORTH);
            loginPanel.setBackground(LIGHT_GREEN);

            entryLogin = new JTextField(15);
            JLabel login = new JLabel("Login");
            login.setForeground(AQUAMARINE);
            loginPanel.add(login, BorderLayout.WEST);
            loginPanel.add(entryLogin, BorderLayout.EAST);
            entryLogin.setBackground(LIGHT_PINK);

            JPanel passwordPanel = new JPanel(new BorderLayout());
            authPanel.add(passwordPanel, BorderLayout.SOUTH);
            passwordPanel.setBackground(LIGHT_GREEN);

            entryPass = new JPasswordField(15);
            JLabel password = new JLabel("Password");
            password.setForeground(AQUAMARINE);
            passwordPanel.add(password, BorderLayout.WEST);
            passwordPanel.add(entryPass, BorderLayout.EAST);
            entryPass.setBackground(LIGHT_PINK);

            JPanel buttonPanel = new JPanel();
            contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
            buttonPanel.setBackground(LIGHT_GREEN);

            signIn = new JButton("Sign In");
            buttonPanel.add(signIn, BorderLayout.WEST);
            signUp = new JButton("Sign Up");
            buttonPanel.add(signUp, BorderLayout.CENTER);
            exit = new JButton("Exit");
            buttonPanel.add(exit, BorderLayout.EAST);

        }
    }
}
