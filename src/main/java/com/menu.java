package com;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class menu extends JFrame {
    private ArrayList<JButton> buttons = new ArrayList<>(); // храним кнопки

    menu() {
        super("Симулятор реальности");
        setBounds(500, 200, 500, 500);
        setLayout(null);
        JTextField textField = new JTextField();
        textField.setBounds(100, 40, 300, 70);
        add(textField);
        for (int i = 0; i < 6; i++) {
            int x = i;
            String[] mas = new String[10];
            for (int j = 0; j < 10; j++)
                mas[j] = j + 1 + "";
            JButton btn = new JButton("Персонаж" + mas[i]);
            btn.setBounds(100, 80 + 50 * (1 + i), 100, 50);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btn.setEnabled(false);
                    if (x == 5) {
                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                AlisaAI.shopstart();
                                return null;
                            }
                            @Override
                            protected void done() {
                                btn.setEnabled(true);
                            }
                        };
                        worker.execute();
                    } else {
                        ChatGUI.start(x, () -> btn.setEnabled(true));
                    }
                }
            });
            add(btn);
            buttons.add(btn);
        }
    }

    public static void main(String[] args) throws IOException {
        menu app = new menu();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }
}