import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Scanner;
class Main extends JFrame {

    int counter = 0;

    Main(){
        super("My title");
        setBounds(500, 200, 1000, 500);
        setLayout(null);

        JButton btn = new JButton("1");
        btn.setBounds(100, 100, 50, 50);

        JTextField textField = new JTextField();
        textField.setBounds(50, 50, 300, 50);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                textField.setText(textField.getText() + counter);
            }
        });

        add(btn);
        add(textField);


    }

    public static void main(String[] args) {
        Main app = new Main();
        app.setVisible(true);
    }
}