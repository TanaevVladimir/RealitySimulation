package com;

import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class person extends JFrame {
    person(int number) {
        super("Персонаж" + number);
        setBounds(500, 200, 500, 500);
        setLayout(null);
        JTextField textField = new JTextField();
        textField.setBounds(100, 40, 300, 70);
        add(textField);
        JButton btn = new JButton("Начать диалог" +"");
        btn.setBounds(100, 90, 100, 50);
        btn.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        btn.setEnabled(false);
                        AlisaAI.personstart(Promt(number));
                    }
            });
            add(btn);
        JButton btn2 = new JButton("Закончить диалог");
        btn2.setBounds(100, 150, 100, 50);
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btn2.setEnabled(false);
                AlisaAI.personstart(Promt(number));
            }
        });
        add(btn2);

    }

    public static void start (int k)  {
        person app = new person(k);
        app.setVisible(true);
    }
    public static String Promt (int k)  {
        return getEnv("PROMT"+k);
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
