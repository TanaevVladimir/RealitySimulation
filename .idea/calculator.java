import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
class Main extends JFrame {

    Main(){
        super("My title");
        setBounds(500, 200, 1000, 500);
        setLayout(null);
        String S = "0123456789+=";
        JTextField textField = new JTextField();
        textField.setBounds(100, 40, 150, 50);
        add(textField);
        for (int i = 0; i<S.length(); i++)
        {
            int x = i;
            JButton btn = new JButton("" + S.charAt(i));
            btn.setBounds(50*(2+i%3), 70+30*(1+(i/3)), 50, 30);
            btn.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (x<10)
                    {
                        textField.setText(textField.getText() + x);
                    }
                    else
                    {
                        if (x==10)
                            textField.setText(textField.getText() + "+");
                        else
                            textField.setText(count(textField.getText()));
                    }

                }
            });
            add(btn);
        }




    }

    public static String count (String str)
    {
        int i = 0;
        int a = 0;
        int b = 0;
        while (i<str.length()) {
            while (str.charAt(i) != '+') {
                i++;
            }
            a = b;
            b = i;
            toDigit();
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.setVisible(true);
    }
}