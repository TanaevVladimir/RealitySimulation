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
        for (int i = 0; i<3; i++)
        {
            int x = i;
            String[] mas = new String [10];
            for (int j=0; j<10; j++)
                mas[j]=j+1+"";
            JButton btn = new JButton(mas[i]);
            btn.setBounds(100*(1+i%3), 90, 100, 50);
            btn.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (x==0) {
                        btn.setEnabled(false);
                    }
                    else if (x==1) {
                        btn.setEnabled(false);
                        AlisaAI.main(null);
                    }


                }
            });
            add(btn);
        }
    }

    public static void main(String[] args) throws IOException {
        menu app = new menu();
        app.setVisible(true);
    }
}