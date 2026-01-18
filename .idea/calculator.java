import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
class Main extends JFrame {

    Main(){
        super("Калькулятор");
        setBounds(500, 200, 500, 500);
        setLayout(null);
        String S = "0123456789+-*÷=←×";
        JTextField textField = new JTextField();
        textField.setBounds(100, 40, 300, 70);
        add(textField);
        for (int i = 0; i<S.length(); i++)
        {
            int x = i;
            JButton btn = new JButton("" + S.charAt(i));
            btn.setBounds(100*(1+i%3), 90+50*(1+(i/3)), 100, 50);
            btn.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (x<10)
                    {
                        textField.setText(textField.getText() + x);
                    }
                    else if (x<14)
                    {
                        int l = textField.getText().length();
                        if(l>0 && !S.substring(10,15).contains(textField.getText().charAt(l-1)+""))
                            textField.setText(textField.getText() + S.charAt(x));
                    }
                    else if (x==14)
                    {
                        int l = textField.getText().length();
                        if (S.substring(10,15).contains(textField.getText().charAt(l-1)+""))
                            textField.setText(textField.getText().substring(0,l-1));
                        else
                            textField.setText(count(textField.getText()));
                    }
                    else if (x==15)
                    {
                        int l = textField.getText().length();
                        if(l>0)
                            textField.setText(textField.getText().substring(0,l-1));
                    }
                    else
                        textField.setText("");

                }
            });
            add(btn);
        }
    }

    public static String countold (String str)
    {
        if (str.equals("Infinity"))
            return "0.0";
        int i = 0;
        if (str.charAt(i)=='-')
            i = 1;
        int a = 0;
        int b = 0;
        double answ = 0;
        while (i<str.length()) {
            while (i<str.length() && (str.charAt(i) >= '0' && str.charAt(i) <= '9' || str.charAt(i) == '.')) {
                i++;
            }
            b = i;
            if (a == 0)
                answ += Double.parseDouble(str.substring(a, b));
            else
                answ = calc(answ,Double.parseDouble(str.substring(a, b)),str.charAt(a-1));
            i++;
            a = i;
        }
        return answ + "";
    }

    public static double calc (double a, double b, char ch)
    {
        if (ch=='+')
            return a+b;
        else if (ch=='-')
            return a-b;
        else if (ch=='*')
            return a*b;
        else
            return a/b;
    }

    public static double toDigit (String str, int a, int b, int c)
    {
        double n = 0;
        for (int i=a; i<=c; i++)
            if (i==b)
                n+=0;
            else
                n+=(str.charAt(i)-'0')*Math.pow(10,b-i-1);
        return n;
    }
    public static String count (String str)
    {
        int[] mas = new int[str.length()];
        boolean flag = true;
        for (int i=0; i<mas.length; i++)
        {
            if (str.charAt(i)=='+' || str.charAt(i)=='-')
            {
                mas[i] = 1;
                flag = false;
            }
            else if (str.charAt(i)=='*' || str.charAt(i)=='÷') {
                mas[i] = 2;
                flag = false;
            }
            else
                mas[i] = 0;
        }
        if (flag)
            return str;
        else {
            int k = 0;
            while (k < mas.length && mas[k] != 2)
                k++;
            if (k < mas.length) {
                int a = k-1;
                while (a >= 0 && mas[a] == 0)
                    a--;
                int b = k+1;
                while (b < mas.length && mas[b] == 0)
                    b++;
                if (a==-1 && b==mas.length)
                    return calc(Double.parseDouble(str.substring(0, k)), Double.parseDouble(str.substring(k+1,b)),str.charAt(k)) + "";
                else if (a==-1)
                    return count(calc(Double.parseDouble(str.substring(0, k)), Double.parseDouble(str.substring(k+1, b)),str.charAt(k)) + str.substring(b));
                else if (b==mas.length)
                    return count(str.substring(0,a+1)+calc(Double.parseDouble(str.substring(a+1, k)), Double.parseDouble(str.substring(k+1, b)),str.charAt(k)));
                else
                    return count(str.substring(0,a+1) + calc(Double.parseDouble(str.substring(a+1, k)), Double.parseDouble(str.substring(k+1, b)),str.charAt(k)) + str.substring(b));

            }
            else
                return countold(str);
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.setVisible(true);
    }
}