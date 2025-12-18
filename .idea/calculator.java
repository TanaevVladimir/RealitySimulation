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
                        if(l>0 && textField.getText().charAt(l-1) != S.charAt(x))
                            textField.setText(textField.getText() + S.charAt(x));
                    }
                    else if (x==14)
                    {
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

    public static String count (String str)
    {
        if (str.equals("Infinity"))
            return "0.0";
        int i = 0;
        int a = 0;
        int b = 0;
        double answ = 0;
        while (i<str.length()) {
            while (i<str.length() && (str.charAt(i) >= '0' && str.charAt(i) <= '9' || str.charAt(i) == '.')) {
                i++;
            }
            b = i;
            if (a == 0 || str.charAt(a - 1) == '+')
                answ += Double.parseDouble(str.substring(a, b));
            else if (str.charAt(a - 1) == '-')
                answ -= Double.parseDouble(str.substring(a, b));
            else if (str.charAt(a - 1) == '*')
                answ *= Double.parseDouble(str.substring(a, b));
            else if (str.charAt(a - 1) == '÷')
                answ /= Double.parseDouble(str.substring(a, b));
            else
                return answ + "";
            i++;
            a = i;
        }
        return answ + "";
    }

    public static int toDigit (String str, int a, int c)
    {
        int n = 0;
        for (int i=a; i<=c; i++)
            n+=(str.charAt(i)-'0')*(int)Math.pow(10,c-i);
        return n;
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
    public static String count1 (String str)
    {
        int[] mas = new int[str.length()];
        boolean flag = true;
        for (int i=0; i<mas.length; i++)
        {
            if (str.charAt(i)=="+" || str.charAt(i)=="-")
            {
                mas[i] = 1;
                flag = false;
            }
            else if (str.charAt(i)=="*" || str.charAt(i)=="÷") {
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
                int a = k+1;
                while (a < mas.length && mas[a] == 0)
                    a++;
                int b = k-1;
                while (b >= 0 && mas[b] == 0)
                    b--;
                if (a==mas.length && b==0)
                    return Double.parseDouble(str.substring(a, k)) + Double.parseDouble(str.substring(k+1, b)) + "";
                else-if {

                }
                return count(str.substring(0, a)) + Double;
            } else {
                k = 0;
                while (mas[k] == 0 && k < mas.length)
                    k++;
                int a = k;
                while (mas[a] == 0 && a < mas.length)
                    ++;
                int b = k;
                while (mas[b] == 0 && b >= 0)
                    b--;
                return count(str.substring(0, a);

            }
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.setVisible(true);
    }
}