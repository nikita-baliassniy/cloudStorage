import javax.swing.*;
import java.awt.*;

/**
 * Класс-обработчик основного изображения фона форм логина и регистрации
 */
public class MainImage extends JPanel {

    Image img = new ImageIcon("client/src/main/resources/cloud1.png").getImage();

    public void paintComponent(Graphics g){
        g.drawImage(img, 0, 0, null);
    }

}
