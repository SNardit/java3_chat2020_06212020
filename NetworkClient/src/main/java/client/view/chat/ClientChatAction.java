package client.view.chat;

import client.controller.ClientController;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;


import static client.view.chat.ClientChat.*;

public class ClientChatAction extends MyWindowChat {

    public final int WINDOW_SIZE_X= 600;
    public final int WINDOW_SIZE_Y= 500;
    public final int WINDOW_START_X = 800;
    public final int WINDOW_START_Y = 100;

    private ClientController controller;

    public ClientChatAction(ClientController controller) {
        this.controller = controller;
        setTitle(controller.getUsername());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(WINDOW_START_X, WINDOW_START_Y, WINDOW_SIZE_X, WINDOW_SIZE_Y);
        setResizable(true);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
        addListeners();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                controller.shutdown();
            }
        });
    }


    public void addListeners() {
        sendButton.addActionListener(e -> sendMessage());
        entryField.addActionListener(e -> sendMessage());
        changeNickButton.addActionListener(e -> changeNickname());
        newNickname.addActionListener(e -> changeNickname());
    }

    private void sendMessage() {
        String message = entryField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        appendOwnMessage(message);

        if (usersList.getSelectedIndex() < 1) {
        controller.sendMessage(message);
        }
        else {
            String nickname = usersList.getSelectedValue();
            controller.sendPrivateMessage(nickname, message);
        }
        entryField.setText(null);
    }

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            ClientChat.chatMessages.append(message);
            chatMessages.append(System.lineSeparator());
        });
    }


    private void appendOwnMessage(String message) {
        appendMessage("Я: " + message);
    }

    public void showError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage);
    }

    public void updateUsers(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            DefaultListModel<String> model = new DefaultListModel<>();
            for (String user : users) {
                model.addElement(user);
            }
            usersList.setModel(model);
        });
    }
    private void changeNickname() {
        String newNick;
        newNick = newNickname.getText().trim();
        controller.changeNickname(newNick);
        newNickname.setText(null);
    }
}
