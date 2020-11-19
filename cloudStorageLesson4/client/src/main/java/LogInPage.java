import common.CommandRequest;
import common.CommandType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class LogInPage extends JFrame {

    private JTextField tfLogin;
    private JPasswordField pfPassword;
    private JFrame frame;

    public LogInPage() {
        Network.getInstance();
        frame = new JFrame("Storage Login Form");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lHeader = new JLabel("Login Form");
        lHeader.setForeground(Color.blue);
        lHeader.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel lLogin = new JLabel("Login");
        JLabel lPassword = new JLabel("Password");
        tfLogin = new JTextField();
        pfPassword = new JPasswordField();
        JButton bSubmit = new JButton("Submit");
        JButton bClear = new JButton("Clear");
        JLabel lNewHere = new JLabel("If you are new here, first - ");
        JLabel lRegistration = new JLabel("register");

        lHeader.setBounds(150, 20, 400, 30);
        lLogin.setBounds(50, 70, 100, 30);
        lPassword.setBounds(50, 110, 100, 30);
        tfLogin.setBounds(150, 70, 200, 30);
        pfPassword.setBounds(150, 110, 200, 30);
        bSubmit.setBounds(150, 160, 98, 30);
        bClear.setBounds(250, 160, 98, 30);
        lNewHere.setBounds(150, 200, 170, 30);
        lRegistration.setBounds(300, 200, 100, 30);
        lRegistration.setForeground(Color.blue);
        lRegistration.setFont(new Font("Times New Roman", Font.ITALIC, 13));

        lRegistration.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        if (evt.getClickCount() == 1) {
                            RegistrationPage registrationPage = new RegistrationPage();
                        }
                    }
                }
        );

        bClear.addActionListener((actionEvent) -> {
            clearFields();
        });

        bSubmit.addActionListener((actionEvent) -> {
            logIn();
        });

        frame.add(lHeader);
        frame.add(lLogin);
        frame.add(tfLogin);
        frame.add(lPassword);
        frame.add(pfPassword);
        frame.add(bSubmit);
        frame.add(bClear);
        frame.add(lNewHere);
        frame.add(lRegistration);

        frame.setSize(450, 300);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);

    }

    private void init() {
        new Thread(() -> Network.getInstance().getClientHandler().setCallback(o ->
        {
            if (o instanceof CommandRequest) {
                CommandRequest commandRequest = (CommandRequest) o;
                if (commandRequest.getCommandType() == CommandType.AUTH
                        && commandRequest.getArg1().equals("OK")) {
                    System.out.println(commandRequest.getArg2());
                    TablePage tablePage = new TablePage(commandRequest.getArg2());
                    frame.dispose();
                } else if (commandRequest.getCommandType() == CommandType.AUTH
                        && commandRequest.getArg1().equals("ERROR")) {
                    JOptionPane.showMessageDialog(this, "Wrong credentials!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        })).start();
    }

    private void logIn() {
        init();
        if (!tfLogin.getText().equals("") && !pfPassword.getText().equals("")) {
            Network.getInstance().sendMessage(new CommandRequest(CommandType.AUTH, tfLogin.getText(), pfPassword.getText()));
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Fill all fields!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        tfLogin.setText("");
        pfPassword.setText("");
    }

    public static void main(String[] args) {
        new LogInPage();
    }
}


