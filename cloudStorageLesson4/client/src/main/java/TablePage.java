import common.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.List;


/**
 * @author user
 */

public class TablePage extends JFrame {

    private String defaultRoot;
    private String login;
    private String currentRoot;
    private JPanel listFilesPanel;
    private JTable tFiles;
    private JList<String> fileList;
    private JFileChooser fileChooser;
    private final String[] columnNames = {"Name", "Size", "Type"};

    public TablePage(String defaultRoot) throws HeadlessException {
        this.defaultRoot = defaultRoot;
        this.currentRoot = defaultRoot;
        this.login = defaultRoot.substring(defaultRoot.lastIndexOf("/") + 1);
        Network.getInstance();
        init();

        JFrame frame = new JFrame("Cloud Storage - " + login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setSize(400, 600);
        frame.setPreferredSize(new Dimension(400, 600));
        frame.setResizable(false);

        String[][] data = {};
        tFiles = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(tFiles);
        frame.pack();
        frame.setLocationRelativeTo(null);

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
                System.out.println(Path.of(currentRoot).getParent().toString());
                currentRoot = currentRoot.substring(0, currentRoot.lastIndexOf("/"));
                System.out.println("Root is now " + currentRoot);
                requestFileTable();
            }
        });

        // Обработчик "проваливания" внутрь папки
        tFiles.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                //   JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = tFiles.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && tFiles.getSelectedRow() != -1) {
                    System.out.println(tFiles.getModel().getValueAt(row, 0));
                    currentRoot += "/" + tFiles.getModel().getValueAt(row, 0);
                    System.out.println("IN ROOT " + currentRoot);
                    requestFileTable();
                }
            }
        });

        // Кнопка скачивания выбранного файла из списка
        downloadButton.addActionListener(actionEvent -> {
            int fileIndex = fileList.getSelectedIndex();
            getFile(currentRoot + "/" + fileList.getModel().getElementAt(fileIndex));
        });

        // Кнопка загрузки файла в хранилище
        uploadButton.addActionListener(actionEvent -> {
            //todo В работе
            int fileIndex = fileList.getSelectedIndex();
            fileChooser.setDialogTitle("Выбор директории");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(TablePage.this);
            if (result == JFileChooser.APPROVE_OPTION) {
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

        frame.getContentPane().add(BorderLayout.NORTH, toolBar);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
        frame.setVisible(true);

        requestFileTable();
    }


    public void init() {

        new Thread(() -> Network.getInstance().getClientHandler().setCallback(o -> {
            if (o instanceof TableRequest) {
                System.out.println("GOT A TABLE");
                TableRequest tableRequest = (TableRequest) o;
                List<FileHandler> fileTable = tableRequest.getFileTable();
                fillFileTable(fileTable);
            } else if (o instanceof CommandRequest) {

//                CommandRequest commandRequest = (CommandRequest) o;
//                if (commandRequest.getCommandType() == CommandType.LIST
//                        && commandRequest.getArg1().equals("OK")) {
//                    try {
//                        System.out.println(commandRequest.getArg2());
//                        Client client = new Client(commandRequest.getArg2());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } else if (commandRequest.getCommandType() == CommandType.AUTH
//                        && commandRequest.getArg1().equals("ERROR")) {
//                    JOptionPane.showMessageDialog(this, "Wrong credentials!",
//                            "ERROR", JOptionPane.ERROR_MESSAGE);
//                }
            }

        })).start();

    }


    private void requestFileTable() {
        System.out.println("REQUEST TO " + currentRoot + " FILES");
        Network.getInstance().sendMessage(new CommandRequest(CommandType.LIST, currentRoot));
    }

    // Метод отображения списка файлов на сервере
    private void fillFileTable(List<FileHandler> fileTable) {

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        while(tableModel.getColumnCount() > 0) {
            tableModel.removeRow(0);
        }

        tableModel.addColumn("Name");
        tableModel.addColumn("Size");
        tableModel.addColumn("Type");
        tFiles.setModel(tableModel);
        tFiles.revalidate();

        if(fileTable.size() > 0) {
            fileTable.stream().filter(FileHandler::isDirectory).forEach(f ->
                    tableModel.addRow(new Object[]{f.getFileName(), "", "Folder"})
            );
            fileTable.stream().filter(f -> !f.isDirectory()).forEach(f ->
                    tableModel.addRow(new Object[]{f.getFileName(), f.getSize() + " KB", "File"})
            );
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < columnNames.length; i++) {
                tFiles.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            tFiles.setModel(tableModel);
            tFiles.revalidate();
        }

    }


    private void sendFile(String filename) {
        //todo Переписать в нетти

        //       try {
//            out.writeUTF("upload");
//            out.writeUTF(filename);
//            File file = new File("client/" + filename);
//            if (file.exists()) {
//                long length = file.length();
//                out.writeLong(length);
//                FileInputStream fileBytes = new FileInputStream(file);
//                int read = 0;
//                byte[] buffer = new byte[256];
//                while ((read = fileBytes.read(buffer)) != -1) {
//                    out.write(buffer, 0, read);
//                }
//                out.flush();
//                String status = in.readUTF();
//                System.out.println(status);
//            } else {
//                System.out.println("NO SUCH FILE ON CLIENT");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void sendMessage(String text) {
//        try {
        //out.writeUTF(text);
//            out.write(text.getBytes());
//            byte[] buffer = new byte[256];
//            int cnt = in.read(buffer);
//
//            System.out.println(new String(buffer, 0, cnt));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void getFile(String fileName) {
        //todo Переписать в нетти
//        try {
//            out.write(("download " + fileName).getBytes());
//            byte[] buffer1 = new byte[1024];
//            int size = in.read(buffer1);
//            File file = new File("client/" + new File(fileName).getName());
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            FileOutputStream fos = new FileOutputStream(file);
//            System.out.println(new String(buffer1, 0, size));
//            fos.write(buffer1, 0, size);
////            byte[] buffer = new byte[256];
////            for (int i = 0; i < (size + 255) / 256; i++) {
////                int read = in.read(buffer);
////                fos.write(buffer, 0, read);
////            }
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }




}
