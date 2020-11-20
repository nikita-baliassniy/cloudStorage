import common.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;


/**
 * Упаковка для пересылки команд между сервером и клиентов
 */

public class TablePage extends JFrame {

    private String defaultRoot;
    private String login;
    private String currentRoot;
    private final String downloadsPath = "client/downloads";
    private JTable tFiles;
    private JFileChooser fileChooser;
    private final String[] columnNames = {"Name", "Size", "Type"};
    private final String iconPath = "client/src/main/resources/cloud2.png";


    public TablePage(String defaultRoot) throws HeadlessException {
        this.defaultRoot = defaultRoot;
        this.currentRoot = defaultRoot;
        this.login = defaultRoot.substring(defaultRoot.lastIndexOf("/") + 1);
        Network.getInstance();
        init();

        JFrame frame = new JFrame("Cloud Storage - " + login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(420, 350));
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
        JButton renameButton = new JButton("Rename");
        JButton removeButton = new JButton("Remove");
        JButton newDirButton = new JButton("New dir");
        toolBar.add(upButton);
        toolBar.add(downloadButton);
        toolBar.add(uploadButton);
        toolBar.add(removeButton);
        toolBar.add(renameButton);
        toolBar.add(newDirButton);
        toolBar.add(helpButton);

        // Кнопка "вверх" для навигации по папкам
        upButton.addActionListener(actionEvent -> {
            if (!currentRoot.equals(defaultRoot)) {
                System.out.println(Path.of(currentRoot).getParent().toString());
                currentRoot = currentRoot.substring(0, currentRoot.lastIndexOf("/"));
                System.out.println("Root is now " + currentRoot);
                requestFileTable();
            }
        });

        // Кнопка хелп
        helpButton.addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(this,
                    "Use buttons: \n<Up dir> - to go to parent folder\n" +
                            "<Download> - to download chosen file to your folder\n" +
                            "<Upload> - to upload file to server in current folder\n" +
                            "<Remove> - to delete chosen file from server\n" +
                            "<Rename> - to rename chosen file on server\n" +
                            "<New dir> - to create new directory in current folder", "Help",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // Обработчик "проваливания" внутрь папки
        tFiles.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (tFiles.getSelectedRow() != -1) {
                    Point point = mouseEvent.getPoint();
                    int row = tFiles.rowAtPoint(point);
                    if (mouseEvent.getClickCount() == 2) {
                        System.out.println(tFiles.getModel().getValueAt(row, 0));
                        currentRoot += "/" + tFiles.getModel().getValueAt(row, 0);
                        System.out.println("IN ROOT " + currentRoot);
                        requestFileTable();
                    }
                }
            }
        });

        // Кнопка скачивания выбранного файла из списка
        downloadButton.addActionListener(actionEvent -> {
            if (tFiles.getSelectedRow() != -1) {
                int indexRow = tFiles.getSelectedRow();
                String fileName = (String) tFiles.getModel().getValueAt(indexRow, 0);
                Network.getInstance().sendMessage(new CommandRequest(CommandType.DOWNLOAD,
                        currentRoot + "/" + fileName));
            }
        });

        // Кнопка загрузки файла в хранилище
        uploadButton.addActionListener(actionEvent -> {
            fileChooser.setDialogTitle("Выбор директории");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(TablePage.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String fileToUpload = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println(fileToUpload);
                try {
                    Network.getInstance().sendMessage(new FileRequest(Paths.get(fileToUpload), currentRoot));
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "There is an error with chosen file! Try another one",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Кнопка переименования выбранного файла из списка
        renameButton.addActionListener(actionEvent -> {
            if (tFiles.getSelectedRow() != -1) {
                int indexRow = tFiles.getSelectedRow();
                String fileName = (String) tFiles.getModel().getValueAt(indexRow, 0);
                String newFileName = JOptionPane.showInputDialog(this,
                        "Input the new name of a file", fileName);
                Network.getInstance().sendMessage(new CommandRequest(CommandType.RENAME,
                        currentRoot + "/" + fileName, currentRoot + "/" + newFileName));
            }
        });

        // Кнопка удаления выбранного файла из списка
        removeButton.addActionListener(actionEvent -> {
            if (tFiles.getSelectedRow() != -1) {
                int indexRow = tFiles.getSelectedRow();
                String fileName = (String) tFiles.getModel().getValueAt(indexRow, 0);
                Network.getInstance().sendMessage(new CommandRequest(CommandType.REMOVE,
                        currentRoot + "/" + fileName));
            }
        });

        newDirButton.addActionListener(actionEvent -> {
            String newFileName = JOptionPane.showInputDialog(this,
                    "Input the name of a new folder",
                    "New folder", JOptionPane.INFORMATION_MESSAGE);
            Network.getInstance().sendMessage(new CommandRequest(CommandType.MKDIR,
                    currentRoot + "/" + newFileName));
        });

        try {
            Image image = ImageIO.read(new File(iconPath));
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            }
            if (o instanceof FileRequest) {
                try {
                    System.out.println("GOT A FILE");
                    FileRequest fileRequest = (FileRequest) o;
                    Path pathToDownload = Paths.get(downloadsPath + "/" + fileRequest.getFileName());
                    //    Files.createFile(pathToDownload);
                    Files.write(pathToDownload, fileRequest.getContent(), StandardOpenOption.CREATE);
                    JOptionPane.showMessageDialog(this,
                            "The file was successfully downloaded to your folder!",
                            "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "There was an error with downloading a file!",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (o instanceof CommandRequest) {
                CommandRequest commandRequest = (CommandRequest) o;
                if (commandRequest.getCommandType() == CommandType.UPLOAD
                        && commandRequest.getArg1().equals("OK")) {
                    try {
                        System.out.println(commandRequest.getArg1());
                        requestFileTable();
                        JOptionPane.showMessageDialog(this,
                                "The file was successfully uploaded to the cloud storage!",
                                "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                                "There was an error with uploading a file!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (commandRequest.getCommandType() == CommandType.REMOVE) {
                    if (commandRequest.getArg1().equals("OK")) {
                        JOptionPane.showMessageDialog(this,
                                "The file was successfully removed from storage!",
                                "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        requestFileTable();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "There was an error with removing a file!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (commandRequest.getCommandType() == CommandType.RENAME) {
                    if (commandRequest.getArg1().equals("OK")) {
                        JOptionPane.showMessageDialog(this,
                                "The file was successfully renamed!",
                                "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        requestFileTable();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "There was an error with renaming a file!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (commandRequest.getCommandType() == CommandType.MKDIR) {
                    if (commandRequest.getArg1().equals("OK")) {
                        JOptionPane.showMessageDialog(this,
                                "The folder was successfully created!",
                                "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        requestFileTable();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "There was an error with creating a folder!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        })).start();
    }

    // Запрос на обновление списка файлов
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
        while (tableModel.getColumnCount() > 0) {
            tableModel.removeRow(0);
        }
        tableModel.addColumn("Name");
        tableModel.addColumn("Size");
        tableModel.addColumn("Type");
        tFiles.setModel(tableModel);
        tFiles.revalidate();
        if (fileTable.size() > 0) {
            fileTable.stream().filter(FileHandler::isDirectory).forEach(f ->
                    tableModel.addRow(new Object[]{f.getFileName(), "", "FOLDER"})
            );
            fileTable.stream().filter(f -> !f.isDirectory()).forEach(f ->
                    tableModel.addRow(new Object[]{f.getFileName(), f.getSize() + " KB", f.getExt()})
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

}
