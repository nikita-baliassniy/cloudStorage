import common.CommandRequest;
import common.CommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private PlaceholderTextField tfLogin;
    private PlaceholderPasswordField pfPassword;
    private PlaceholderPasswordField pfPassword2;
    private final String iconPath = "client/src/main/resources/cloud2.png";
    public static final Logger LOGGER = LogManager.getLogger(MainClientPage.class);

    public RegistrationPage() {
        frame = new JFrame("Registration Form");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lHeader = new JLabel("Registration");
        lHeader.setForeground(Color.blue);
        lHeader.setFont(new Font("Serif", Font.BOLD, 20));

        tfLogin = new PlaceholderTextField();
        tfLogin.setPlaceholder("Login");
        pfPassword = new PlaceholderPasswordField();
        pfPassword.setPlaceholder("Password");
        pfPassword2 = new PlaceholderPasswordField();
        pfPassword2.setPlaceholder("Repeat password");
        JButton bRegister = new JButton("Register");
        JButton bClear = new JButton("Clear");

        lHeader.setBounds(190, 70, 400, 30);
        tfLogin.setBounds(120, 110, 200, 25);
        pfPassword.setBounds(120, 145, 200, 25);
        pfPassword2.setBounds(120, 180, 200, 25);
        bRegister.setBounds(120, 220, 98, 25);
        bClear.setBounds(220, 220, 98, 25);

        bClear.addActionListener((actionEvent) -> {
            clearFields();
        });

        bRegister.addActionListener((actionEvent) -> {
            register();
        });

        MainImage mainImage = new MainImage();
        mainImage.setBounds(5, 5, 450, 300);
        frame.add(lHeader);
        frame.add(tfLogin);
        frame.add(pfPassword);
        frame.add(pfPassword2);
        frame.add(bRegister);
        frame.add(bClear);
        frame.add(mainImage);
        frame.setSize(450, 310);
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
                    LOGGER.info("Successful registration !");
                    frame.dispose();
                } else if (commandRequest.getCommandType() == CommandType.REGISTER
                        && commandRequest.getArg1().equals("BUSY")) {
                    JOptionPane.showMessageDialog(this, "This login is already in use!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    LOGGER.warn("Registration attempt failed due to existence of chosen login");
                }
            }
        })).start();
    }

    private void register() {
        if (!tfLogin.getText().equals("") && !pfPassword.getText().equals("") && !pfPassword2.getText().equals("")) {
            if (pfPassword.getText().equals(pfPassword2.getText())) {
                Network.getInstance().sendMessage(new CommandRequest(CommandType.REGISTER, tfLogin.getText(), pfPassword.getText()));
                LOGGER.info("Registration attempt for user <" + tfLogin.getText() + ">");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Passwords are different!",
                        "ERROR", JOptionPane.ERROR_MESSAGE);
                LOGGER.warn("Registration attempt failed due to different passwords input");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Fill all fields!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            LOGGER.warn("Registration attempt failed due to empty fields");
        }
    }

    private void clearFields() {
        tfLogin.setText("");
        pfPassword.setText("");
        pfPassword2.setText("");
    }

}
