package client.view.chat;


import javax.swing.*;
import java.awt.*;

import static client.view.auth.AuthDialog.*;

public class ClientChat {

    public static JPanel mainPanel;
    public static JButton sendButton;
    public static JButton changeNickButton;
    public static JTextField entryField;
    public static JTextField newNickname;
    public static JList <String> usersList;
    public static JTextArea chatMessages;

    static class MyWindowChat extends JFrame {


        public MyWindowChat() {

            mainPanel = new JPanel(new BorderLayout());
            add(mainPanel);

            JPanel usersPane = new JPanel(new BorderLayout());
            usersPane.setPreferredSize(new Dimension(150, getHeight()));
            mainPanel.add(usersPane, BorderLayout.WEST);

            JPanel changeNickPane = new JPanel(new BorderLayout());
            changeNickPane.setPreferredSize(new Dimension(getWidth(), 55));
            usersPane.add(changeNickPane, BorderLayout.NORTH);
            newNickname = new JTextField();
            changeNickPane.add(newNickname, BorderLayout.NORTH);
            changeNickButton = new JButton("Change nick");
            changeNickButton.setPreferredSize(new Dimension(100,10));
            changeNickPane.add(changeNickButton);
            changeNickPane.setBackground(BRIGHT_GREEN);

            usersList = new JList<>();
            usersPane.add(usersList, BorderLayout.CENTER);
            usersList.setPreferredSize(usersPane.getPreferredSize());
            usersList.setSelectionBackground(BRIGHT_GREEN);
            usersList.setBackground(LIGHT_GREEN);
            usersList.setForeground(AQUAMARINE);
            usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


            JPanel chatPanel = new JPanel(new BorderLayout());
            mainPanel.add(chatPanel);

            JPanel dialogPanel = new JPanel(new BorderLayout());
            chatPanel.add(dialogPanel);

            chatMessages = new JTextArea();
            JScrollPane scrollChatMessages = new JScrollPane(chatMessages);
            dialogPanel.add(scrollChatMessages, BorderLayout.CENTER);
            chatMessages.setLineWrap(true);
            chatMessages.setWrapStyleWord(true);
            chatMessages.setBackground(LIGHT_PINK);
            chatMessages.setForeground(AQUAMARINE);
            chatMessages.setText("Your previous messages: \n");
            chatMessages.setEditable(false);


            JPanel sendMessagePanel = new JPanel();
            chatPanel.add(sendMessagePanel, BorderLayout.PAGE_END);
            sendMessagePanel.setLayout(new BorderLayout());
            sendMessagePanel.setBackground(BRIGHT_GREEN);

            entryField = new JTextField();
            sendMessagePanel.add(entryField, BorderLayout.CENTER);
            sendButton = new JButton("Send");
            sendMessagePanel.add(sendButton, BorderLayout.EAST);

        }
    }
}
