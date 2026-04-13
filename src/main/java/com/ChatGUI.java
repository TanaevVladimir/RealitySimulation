package com;

import io.github.cdimascio.dotenv.Dotenv;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private String apiKey;
    private String modelUri;
    private java.util.ArrayList<String> messageHistory;
    private int characterId;
    private String npcname;
    private JLabel reputationLabel;
    private Runnable onCloseCallback;

    public ChatGUI(String apiKey, String folderId, int x, Runnable onCloseCallback) {
        this.apiKey = apiKey;
        this.modelUri = "gpt://" + folderId + "/yandexgpt/rc";
        this.characterId = x;
        this.messageHistory = new java.util.ArrayList<>();
        this.npcname = getEnv("NAME"+x);
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
        if (x==5)
        {
            JButton buyButton = new JButton("Купить");
            buyButton.setBounds(350, 30, 100, 50);
            buyButton.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String info = AlisaAI.sendRequest(apiKey,getEnv("SHOP") + messageHistory.get(messageHistory.size() - 2),modelUri);
                        System.out.println(info);
                        double price = Double.parseDouble(messageHistory.get(messageHistory.size() - 1));
                        String product = info.substring(0, info.indexOf(';'));
                        int amount = Integer.parseInt(info.substring(info.indexOf(';')+1));
                        if (AlisaAI.getBalance()>=price) {
                            AlisaAI.addtoInventory(product, amount);
                            AlisaAI.changeBalance(-price);
                            System.out.println(AlisaAI.getBalance());
                        }
                    }
                    catch (Exception er)
                    {
                        er.printStackTrace();
                    }
                }
            });
            add(buyButton);
            JButton sellButton = new JButton("Продать");
            sellButton.setBounds(350, 130, 100, 50);
            sellButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String info = AlisaAI.sendRequest(apiKey,getEnv("SHOP") + messageHistory.get(messageHistory.size() - 2),modelUri);
                        double price = Double.parseDouble(messageHistory.get(messageHistory.size() - 1));
                        String product = info.substring(0, info.indexOf(';'));
                        int amount = Integer.parseInt(info.substring(info.indexOf(';')+1));
                        if (AlisaAI.getBalance() >= price) {
                            boolean w = AlisaAI.removefromInventory(product,amount);
                            if (w)
                                AlisaAI.changeBalance(price / 2);
                            System.out.println(AlisaAI.getBalance());
                        }
                    }
                    catch (Exception er)
                    {
                        er.printStackTrace();
                    }

                }
            });
            add(sellButton);

        }
        setTitle("Персонаж " + x);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reputationLabel = new JLabel();
        reputationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        updateReputationDisplay();
        topPanel.add(reputationLabel);
        add(topPanel, BorderLayout.NORTH);

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
        updateReputationDisplay();
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return AlisaAI.sendRequest(apiKey, messageHistory.toString() + " Текущая репутация: " + AlisaAI.getReputation(characterId), modelUri);
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    messageHistory.add(response);
                    appendToChat(npcname, response);
                    updateReputationAfterDialog(userMessage, response);
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

    private void updateReputationDisplay() {
        int rep = AlisaAI.getReputation(characterId);
        reputationLabel.setText("Репутация: " + rep);
    }

    private void updateReputationAfterDialog(String userMsg, String assistantMsg) {
        try {
            int reputation = AlisaAI.countReputation(apiKey, modelUri, characterId, userMsg, assistantMsg);
            AlisaAI.saveReputation(characterId, reputation);
            updateReputationDisplay();
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении репутации: " + e.getMessage());
        }
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