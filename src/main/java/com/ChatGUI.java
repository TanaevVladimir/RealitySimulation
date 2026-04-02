package com;

import io.github.cdimascio.dotenv.Dotenv;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private String apiKey;
    private String modelUri;
    private java.util.ArrayList<String> messageHistory;
    private int characterId;
    private Runnable onCloseCallback; // коллбэк для активации кнопки

    public ChatGUI(String apiKey, String folderId, int x, Runnable onCloseCallback) {
        this.apiKey = apiKey;
        this.modelUri = "gpt://" + folderId + "/yandexgpt/rc";
        this.characterId = x;
        this.messageHistory = new java.util.ArrayList<>();
        ArrayList<String> loadedHistory = AlisaAI.loadHistory(characterId);
        if (loadedHistory != null) {
            this.messageHistory.addAll(loadedHistory);
            System.out.println("Загружена история для персонажа " + characterId + ": " + loadedHistory.size() + " сообщений");
        } else {
            String promt = getEnv("PROMT" + characterId);
            if (promt != null) this.messageHistory.add(promt);
        }

        this.onCloseCallback = onCloseCallback;
        initUI(characterId);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AlisaAI.saveHistory(characterId, messageHistory);
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
                dispose();
            }
        });
    }

    private void initUI(int x) {
        setTitle("Персонаж " + x);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // важно!

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Отправить");
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        if (userMessage.isEmpty()) return;
        messageHistory.add(userMessage);
        appendToChat("Вы", userMessage);
        inputField.setText("");
        inputField.setEnabled(false);
        sendButton.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return AlisaAI.sendRequest(apiKey, messageHistory.toString(), modelUri);
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    messageHistory.add(response);
                    appendToChat("Алиса", response);
                } catch (Exception e) {
                    appendToChat("Ошибка", e.getMessage());
                    e.printStackTrace();
                } finally {
                    inputField.setEnabled(true);
                    sendButton.setEnabled(true);
                    inputField.requestFocus();
                }
            }
        };
        worker.execute();
    }

    private void appendToChat(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(sender + ": " + message + "\n\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void start(int x, Runnable onCloseCallback) {
        String apiKey = getEnv("YANDEX_API_KEY");
        String folderId = getEnv("YANDEX_FOLDER_ID");
        if (apiKey == null || folderId == null) {
            System.err.println("Установите переменные окружения YANDEX_API_KEY и YANDEX_FOLDER_ID");
            return;
        }
        SwingUtilities.invokeLater(() -> new ChatGUI(apiKey, folderId, x, onCloseCallback).setVisible(true));
    }

    public static String getEnv(String key) {
        try {
            Dotenv dotenv = Dotenv.load();
            return dotenv.get(key);
        } catch (Exception e) {
            return null;
        }
    }
}