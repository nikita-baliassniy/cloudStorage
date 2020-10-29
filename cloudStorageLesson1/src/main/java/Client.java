import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

/**
 * Client command: upload fileName | download fileName
 *
 * @author user
 */

public class Client extends JFrame {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public Client() throws HeadlessException, IOException {
        socket = new Socket("localhost", 8189);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        setSize(300, 300);
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JButton send = new JButton("SUBMIT COMMAND");
        JTextField text = new JTextField();
        send.addActionListener(a -> {
            String[] cmd = text.getText().split(" ");
            if (cmd[0].equals("upload")) {
                sendFile(cmd[1]);
            }
            if (cmd[0].equals("download")) {
                getFile(cmd[1]);
            }
        });
        panel.add(text);
        panel.add(send);
        add(panel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                sendMessage("exit");
            }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * New added method of sending file to server
     */
    private void getFile(String fileName) {
        try {
            out.writeUTF("download");
            out.writeUTF(fileName);
            if (in.readBoolean()) {
                File file = new File("client/" + fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                long size = in.readLong();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[256];
                for (int i = 0; i < (size + 255) / 256; i++) {
                    int read = in.read(buffer);
                    fos.write(buffer, 0, read);
                }
                fos.close();
                out.writeUTF("OK");
            } else {
                System.out.println("NO SUCH FILE ON SERVER");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String filename) {
        try {
            out.writeUTF("upload");
            out.writeUTF(filename);
            File file = new File("client/" + filename);
            if (file.exists()) {
                long length = file.length();
                out.writeLong(length);
                FileInputStream fileBytes = new FileInputStream(file);
                int read = 0;
                byte[] buffer = new byte[256];
                while ((read = fileBytes.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                out.flush();
                String status = in.readUTF();
                System.out.println(status);
            } else {
                System.out.println("NO SUCH FILE ON CLIENT");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String text) {
        try {
            out.writeUTF(text);
            System.out.println(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }

}
