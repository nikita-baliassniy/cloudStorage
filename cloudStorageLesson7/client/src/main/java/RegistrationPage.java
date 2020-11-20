import common.CommandRequest;
import common.CommandType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Класс, отвечающий за страницу регистрации пользователя
 */
public class RegistrationPage extends JFrame {

    private JFrame frame;
    private JTextField tfLogin;
    private JPasswordField pfPassword;
    private JPasswordField pfPassword2;
    private final String iconPath = "client/src/main/resources/cloud2.png";

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

        lHeader.setBounds(160, 20, 400, 30);
        lLogin.setBounds(25, 70, 100, 30);
        lPassword.setBounds(25, 110, 100, 30);
        lPassword2.setBounds(25, 150, 140, 30);

        tfLogin.setBounds(160, 70, 200, 30);
        pfPassword.setBounds(160, 110, 200, 30);
        pfPassword2.setBounds(160, 150, 200, 30);

        bRegister.setBounds(160, 200, 98, 30);
        bClear.setBounds(260, 200, 98, 30);

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
        frame.setLocationRelativeTo(null);

        try {
            Image image = ImageIO.read(new File(iconPath));
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void register() {
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
    }

    private void clearFields() {
        tfLogin.setText("");
        pfPassword.setText("");
        pfPassword2.setText("");
    }

}
