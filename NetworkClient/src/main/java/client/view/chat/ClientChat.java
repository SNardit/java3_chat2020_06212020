package client.view.chat;


import javax.swing.*;
import java.awt.*;

import static client.view.auth.AuthDialog.*;

public class ClientChat {

    public static JPanel mainPanel;
    public static JButton sendButton;
    public static JTextField entryField;
    public static JList <String> usersList;
    public static JTextArea chatMessages;

    static class MyWindowChat extends JFrame {


        public MyWindowChat() {

            mainPanel = new JPanel(new BorderLayout());
            add(mainPanel);

            JPanel usersPane = new JPanel(new BorderLayout());
            usersPane.setPreferredSize(new Dimension(150, getHeight()));
            mainPanel.add(usersPane, BorderLayout.WEST);

            //String[] users = {"All", "nick1", "nick2", "nick3", "nick4", "nick5"};
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
            chatMessages.setText("Ваши предыдущие сообщения: \n");
            chatMessages.setEditable(false);


            JPanel sendMessagePanel = new JPanel();
            chatPanel.add(sendMessagePanel, BorderLayout.PAGE_END);
            sendMessagePanel.setLayout(new BorderLayout());
            sendMessagePanel.setBackground(BRIGHT_GREEN);

            entryField = new JTextField();
            sendMessagePanel.add(entryField, BorderLayout.CENTER);
            sendButton = new JButton("Отправить");
            sendMessagePanel.add(sendButton, BorderLayout.EAST);

        }
    }
}
