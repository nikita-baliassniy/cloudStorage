import common.CommandRequest;
import common.CommandType;

import javax.swing.*;
import java.awt.*;


public class RegistrationPage extends JFrame {

    private JFrame frame;
    private JTextField tfLogin;
    private JPasswordField pfPassword;
    private JPasswordField pfPassword2;


    public RegistrationPage() {

        frame = new JFrame("Registration Login Form");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JLabel lHeader = new JLabel("Registration Form");
        lHeader.setForeground(Color.blue);
        lHeader.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel lLogin = new JLabel("Enter login");
        JLabel lPassword = new JLabel("Enter password");
        JLabel lPassword2 = new JLabel("Repeat your password");
        tfLogin = new JTextField();
        pfPassword = new JPasswordField();
        pfPassword2 = new JPasswordField();
        JButton bRegister = new JButton("Register");
        JButton bClear = new JButton("Clear");

        lHeader.setBounds(150, 20, 400, 30);

        lLogin.setBounds(35, 70, 100, 30);
        lPassword.setBounds(35, 110, 100, 30);
        lPassword2.setBounds(35, 150, 100, 30);

        tfLogin.setBounds(150, 70, 200, 30);
        pfPassword.setBounds(150, 110, 200, 30);
        pfPassword2.setBounds(150, 150, 200, 30);

        bRegister.setBounds(150, 200, 98, 30);
        bClear.setBounds(250, 200, 98, 30);

        bClear.addActionListener((actionEvent) -> {
            clearFields();
        });

        bRegister.addActionListener((actionEvent) -> {
            register();
        });

        frame.add(lHeader);
        frame.add(lLogin);
        frame.add(tfLogin);
        frame.add(lPassword);
        frame.add(pfPassword);
        frame.add(lPassword2);
        frame.add(pfPassword2);
        frame.add(bRegister);
        frame.add(bClear);

        frame.setSize(450, 300);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);

        init();

    }

    private void init() {
        new Thread(() -> Network.getInstance().getClientHandler().setCallback(o ->
        {
            if (o instanceof CommandRequest) {
                CommandRequest commandRequest = (CommandRequest) o;
                if (commandRequest.getCommandType() == CommandType.REGISTER
                        && commandRequest.getArg1().equals("OK")) {
                    JOptionPane.showMessageDialog(this, "Registration was successful!",
                            "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                } else if (commandRequest.getCommandType() == CommandType.REGISTER
                        && commandRequest.getArg1().equals("BUSY")) {
                    JOptionPane.showMessageDialog(this, "This login is already in use!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        })).start();
    }

    private boolean register() {
        if (!tfLogin.getText().equals("") && !pfPassword.getText().equals("") && !pfPassword2.getText().equals("")) {
            if (pfPassword.getText().equals(pfPassword2.getText())) {
                Network.getInstance().sendMessage(new CommandRequest(CommandType.REGISTER, tfLogin.getText(), pfPassword.getText()));
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Passwords are different!",
                        "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Fill all fields!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private void clearFields() {
        tfLogin.setText("");
        pfPassword.setText("");
        pfPassword2.setText("");
    }

}
