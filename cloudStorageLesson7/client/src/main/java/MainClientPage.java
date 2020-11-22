import common.CommandRequest;
import common.CommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * Запускаемая часть клиента, отвечает за страницу авторизации
 */
public class MainClientPage extends JFrame {

    private PlaceholderTextField tfLogin;
    private PlaceholderPasswordField pfPassword;
    private JFrame frame;
    private final String iconPath = "client/src/main/resources/cloud2.png";
    public static final Logger LOGGER = LogManager.getLogger(MainClientPage.class);

    public MainClientPage() {
        Network.getInstance();
        frame = new JFrame("Storage Login Form");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lHeader = new JLabel("Login Form");
        lHeader.setForeground(Color.blue);
        lHeader.setFont(new Font("Serif", Font.BOLD, 20));

        tfLogin = new PlaceholderTextField();
        tfLogin.setPlaceholder("LOGIN");
        pfPassword = new PlaceholderPasswordField();
        pfPassword.setPlaceholder("Password");
        JButton bSubmit = new JButton("Submit");
        JButton bClear = new JButton("Clear");
        JLabel lNewHere = new JLabel("If you are new here, first - ");
        JLabel lRegistration = new JLabel("register");

        lHeader.setBounds(190, 70, 400, 30);
        tfLogin.setBounds(120, 100, 200, 25);
        pfPassword.setBounds(120, 140, 200, 25);
        bSubmit.setBounds(120, 180, 98, 25);
        bClear.setBounds(220, 180, 98, 25);
        lNewHere.setBounds(120, 210, 170, 30);
        lRegistration.setBounds(270, 211, 100, 30);
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

        MainImage mainImage = new MainImage();
        mainImage.setBounds(5, 5, 450, 300);
        frame.add(lHeader);
        frame.add(tfLogin);
        frame.add(pfPassword);
        frame.add(bSubmit);
        frame.add(bClear);
        frame.add(lNewHere);
        frame.add(lRegistration);
        frame.setSize(450, 310);
        frame.setLayout(null);
        frame.add(mainImage);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        try {
            Image image = ImageIO.read(new File(iconPath));
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        new Thread(() -> Network.getInstance().getClientHandler().setCallback(o ->
        {
            if (o instanceof CommandRequest) {
                CommandRequest commandRequest = (CommandRequest) o;
                if (commandRequest.getCommandType() == CommandType.AUTH
                        && commandRequest.getArg1().equals("OK")) {
                    LOGGER.info("Authorization successful");
                    TablePage tablePage = new TablePage(commandRequest.getArg2());
                    frame.dispose();
                } else if (commandRequest.getCommandType() == CommandType.AUTH
                        && commandRequest.getArg1().equals("ERROR")) {
                    JOptionPane.showMessageDialog(this, "Wrong credentials!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    LOGGER.warn("Authorization attempt failed due to wrong credentials");
                }
            }
        })).start();
    }

    private void logIn() {
        init();
        if (!tfLogin.getText().equals("") && !pfPassword.getText().equals("")) {
            Network.getInstance().sendMessage(new CommandRequest(CommandType.AUTH, tfLogin.getText(), pfPassword.getText()));
            LOGGER.info("Authorization attempt for user <" + tfLogin.getText() + ">");
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Fill all fields!",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            LOGGER.warn("Authorization attempt failed due to empty fields");
        }
    }

    private void clearFields() {
        tfLogin.setText("");
        pfPassword.setText("");
    }

    public static void main(String[] args) {
        new MainClientPage();
    }
}


