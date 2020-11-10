
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;


/**
 * @author user
 */

public class Client extends JFrame {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    // todo Подтягивать defaultRoot из базы после авторизации
    private String defaultRoot = "server";
    private String currentRoot = defaultRoot;
    private JPanel listFilesPanel;
    private JList<String> fileList;
    private JFileChooser fileChooser;

    public Client() throws HeadlessException, IOException {
        socket = new Socket("localhost", 8189);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        JFrame frame = new JFrame("Cloud Storage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setResizable(false);

        fileChooser = new JFileChooser();

        JToolBar toolBar = new JToolBar();
        JButton downloadButton = new JButton("Download");
        JButton helpButton = new JButton("Help");
        JButton upButton = new JButton("Up dir");
        JButton uploadButton = new JButton("Upload");
        toolBar.add(downloadButton);
        toolBar.add(uploadButton);

        //todo Описать хелп
        toolBar.add(helpButton);

        toolBar.add(upButton);

        // Кнопка "вверх" для навигации по папкам
        upButton.addActionListener(actionEvent -> {
            if (!currentRoot.equals(defaultRoot)) {
                currentRoot = currentRoot.substring(0, currentRoot.lastIndexOf("/"));
                System.out.println("Root is now " + currentRoot);
                fillFileList();
            }
        });

        // Кнопка скачивания выбранного файла из списка
        downloadButton.addActionListener(actionEvent -> {
            int fileIndex = fileList.getSelectedIndex();
            getFile(currentRoot + "/" + fileList.getModel().getElementAt(fileIndex));
        });

        // Кнопка загрузки файла в хранилище
        uploadButton.addActionListener(actionEvent -> {
            int fileIndex = fileList.getSelectedIndex();
            fileChooser.setDialogTitle("Выбор директории");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(Client.this);
            if (result == JFileChooser.APPROVE_OPTION ) {
                System.out.println(fileChooser.getSelectedFile().getAbsolutePath());
                //todo sendFile()
            }
        });

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter Text");
        // todo В поле можно будет вводить команды терминала из прошлой лабы
        JTextField tf = new JTextField(40);
        JButton send = new JButton("Send");
        JButton reset = new JButton("Reset");
        panel.add(label);
        panel.add(tf);
        panel.add(send);
        panel.add(reset);

        listFilesPanel = new JPanel();

        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.NORTH, toolBar);
        frame.getContentPane().add(BorderLayout.CENTER, listFilesPanel);
        frame.setVisible(true);

        fillFileList();

        //        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                super.windowClosed(e);
//                sendMessage("exit");
//            }
//        });

    }

    // Метод отображения списка файлов на сервере
    private void fillFileList() {

        listFilesPanel.removeAll();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        File rootDirectory = new File(currentRoot);
        if (rootDirectory.exists() && rootDirectory.listFiles().length > 0) {
            for (int i = 0; i < rootDirectory.listFiles().length; i++) {
                File currentFile = new File(rootDirectory.listFiles()[i].getAbsolutePath());
                if (currentFile.isDirectory()) {
                    listModel.addElement("> " + rootDirectory.listFiles()[i].getName());
                } else {
                    listModel.addElement(rootDirectory.listFiles()[i].getName());
                }
            }
        }
        fileList = new JList<>(listModel);
        fileList.setBounds(0, 0, 400, 500);

        // Обработчик "проваливания" внутрь папки
        fileList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    String fileName = currentRoot + "/" +
                            ((String) list.getModel().getElementAt(index)).replaceAll("> ", "");
                    System.out.println(new File(fileName).getAbsolutePath());
                    if (new File(fileName).isDirectory()) {
                        currentRoot = fileName;
                        System.out.println(currentRoot);
                        fillFileList();
                    }
                }
            }
        });
        listFilesPanel.add(fileList);
        listFilesPanel.setSize(400, 500);
        listFilesPanel.setVisible(true);
        listFilesPanel.setLayout(null);
        listFilesPanel.repaint();
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
            //out.writeUTF(text);
            out.write(text.getBytes());
            byte[] buffer = new byte[256];
            int cnt = in.read(buffer);

            System.out.println(new String(buffer, 0, cnt));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFile(String fileName) {
        try {
            out.write(("download " + fileName).getBytes());
            byte[] buffer1 = new byte[1024];
            int size = in.read(buffer1);
            File file = new File("client/" + new File(fileName).getName());
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            System.out.println(new String(buffer1, 0, size));
            fos.write(buffer1, 0, size);
//            byte[] buffer = new byte[256];
//            for (int i = 0; i < (size + 255) / 256; i++) {
//                int read = in.read(buffer);
//                fos.write(buffer, 0, read);
//            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        new Client();
    }

}
