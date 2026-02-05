package com;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class shop extends JFrame {

    shop() {
        super("Магазин");
        setBounds(500, 200, 500, 500);
        setLayout(null);
        JTextField textField = new JTextField();
        textField.setBounds(100, 40, 300, 70);
        add(textField);
        JButton btn = new JButton("Цена");
        btn.setBounds(200 , 150 , 100, 50);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        textField.setText(Deepseek.price(textField.getText()));
                    } catch (IOException er) {
                        throw new RuntimeException(er);
                    }
                }
            });
            add(btn);
        }

    public static void main(String[] args) throws IOException {
        shop app = new shop();
        app.setVisible(true);
        Deepseek.shopstart();
    }
}
