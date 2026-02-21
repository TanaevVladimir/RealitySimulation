package com;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class menu extends JFrame {

    menu() {
        super("Симулятор реальности");
        setBounds(500, 200, 500, 500);
        setLayout(null);
        JTextField textField = new JTextField();
        textField.setBounds(100, 40, 300, 70);
        add(textField);
        JButton btn = new JButton("Гулять");
        btn.setBounds(200 , 150 , 100, 50);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    btn.setEnabled(false);
                    AlisaAI.main(null);
                    ArrayList<String> text = new ArrayList<>;
                    text = AlisaAI.field;
                    while(true)
                    {
                        while (text == AlisaAI.field) {}
                        textField.setText(textField.getText()+AlisaAI.field.get(text.size()));
                    }
            }
        });
        add(btn);
    }

    public static void main(String[] args) throws IOException {
        menu app = new menu();
        app.setVisible(true);
    }
}