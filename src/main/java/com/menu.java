package com;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import static com.ChatGUI.getEnv;

public class menu extends JFrame {
    private ArrayList<JButton> buttons = new ArrayList<>(); // храним кнопки

    menu() {
        super("Симулятор реальности");
        setBounds(500, 200, 500, 500);
        setLayout(null);
        JTextField textField = new JTextField();
        textField.setBounds(100, 40, 300, 70);
        add(textField);
        for (int i = 0; i < 5; i++) {
            int x = i+1;
            int[] mas = new int[10];
            for (int j = 0; j < 10; j++)
                mas[j] = j;
            JButton btn = new JButton(getEnv("NAME"+ mas[i+1]));
            btn.setBounds(150, 80 + 50 * (1 + i), 200, 50);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    btn.setEnabled(false);
                    ChatGUI.start(x, () -> btn.setEnabled(true));
                    }
            });
            add(btn);
            buttons.add(btn);
        }
        JButton inventoryButton = new JButton("Инвентарь");
        inventoryButton.setBounds(150, 80 + 50 * 6, 200, 50);
        inventoryButton.addActionListener(e -> {
            InventoryGUI invGui = new InventoryGUI();
            invGui.setVisible(true);
        });
        add(inventoryButton);
    }

    public static void main(String[] args) throws IOException {
        menu app = new menu();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }
}