import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
class Main extends JFrame {

    int counter = 0;

    Main(){
        super("My title");
        setBounds(500, 200, 1000, 500);
        setLayout(null);
        String S = "0123456789+=";
        JTextField textField = new JTextField();
        textField.setBounds(50, 50, 300, 50);
        add(textField);
        for (int x = 0; x<10; x++)
        {
            JButton btn = new JButton(toString();
            btn.setBounds(100+x*10, 100+x*10, 50, 50);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    counter++;
                    textField.setText(textField.getText() + counter);

                }
            });
            add(btn);
        }




    }

    public static void main(String[] args) {
        Main app = new Main();
        app.setVisible(true);
    }
}