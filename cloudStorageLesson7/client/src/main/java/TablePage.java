import common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Упаковка для пересылки команд между сервером и клиентов
 */

public class TablePage extends JFrame {

    private String defaultRoot;
    private String login;
    private String currentRoot;
    private final String downloadsPath = "client/downloads";
    private JFrame frame;
    private JTable tFiles;
    private JFileChooser fileChooser;
    private final Map<String, Integer> columnNames = new HashMap<>() {{
        put("Icon", 0);
        put("Name", 1);
        put("Size", 2);
        put("Type", 3);
    }};
    private final String imgPath = "client/src/main/resources/";
    public static final Logger LOGGER = LogManager.getLogger(MainClientPage.class);

    public TablePage(String defaultRoot) throws HeadlessException {
        this.defaultRoot = defaultRoot;
        this.currentRoot = defaultRoot;
        this.login = defaultRoot.substring(defaultRoot.lastIndexOf("/") + 1);
        Network.getInstance();
        init();

        frame = new JFrame("Cloud Storage - " + login);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(505, 450));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        try {
            Image image = ImageIO.read(new File(imgPath + "cloud2.png"));
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tFiles = new JTable();
        JScrollPane scrollPane = new JScrollPane(tFiles);

        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(100, 450));
        panel.setMinimumSize(new Dimension(100, 450));
        panel.setPreferredSize(new Dimension(100, 450));

        JButton downloadButton = new JButton("Download");
        JButton helpButton = new JButton("Help");
        JButton upButton = new JButton("Up dir");
        JButton uploadButton = new JButton("Upload");
        JButton renameButton = new JButton("Rename");
        JButton removeButton = new JButton("Remove");
        JButton newDirButton = new JButton("New dir");
        JButton aboutButton = new JButton("About");

        upButton.setMargin(new Insets(10, 10, 10, 10));
        upButton.setVerticalAlignment(SwingConstants.CENTER);
        upButton.setHorizontalAlignment(SwingConstants.CENTER);
        upButton.setHorizontalTextPosition(SwingConstants.CENTER);
        upButton.setVerticalTextPosition(SwingConstants.CENTER);
        upButton.setPreferredSize(new Dimension(85, 30));

        helpButton.setMargin(new Insets(10, 10, 10, 10));
        helpButton.setVerticalAlignment(SwingConstants.CENTER);
        helpButton.setHorizontalAlignment(SwingConstants.CENTER);
        helpButton.setHorizontalTextPosition(SwingConstants.CENTER);
        helpButton.setVerticalTextPosition(SwingConstants.CENTER);
        helpButton.setPreferredSize(new Dimension(85, 30));

        downloadButton.setMargin(new Insets(10, 10, 10, 10));
        downloadButton.setVerticalAlignment(SwingConstants.CENTER);
        downloadButton.setHorizontalAlignment(SwingConstants.CENTER);
        downloadButton.setHorizontalTextPosition(SwingConstants.CENTER);
        downloadButton.setVerticalTextPosition(SwingConstants.CENTER);
        downloadButton.setPreferredSize(new Dimension(85, 30));

        uploadButton.setMargin(new Insets(10, 10, 10, 10));
        uploadButton.setVerticalAlignment(SwingConstants.CENTER);
        uploadButton.setHorizontalAlignment(SwingConstants.CENTER);
        uploadButton.setHorizontalTextPosition(SwingConstants.CENTER);
        uploadButton.setVerticalTextPosition(SwingConstants.CENTER);
        uploadButton.setPreferredSize(new Dimension(85, 30));

        removeButton.setMargin(new Insets(10, 10, 10, 10));
        removeButton.setVerticalAlignment(SwingConstants.CENTER);
        removeButton.setHorizontalAlignment(SwingConstants.CENTER);
        removeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        removeButton.setVerticalTextPosition(SwingConstants.CENTER);
        removeButton.setPreferredSize(new Dimension(85, 30));

        renameButton.setMargin(new Insets(10, 10, 10, 10));
        renameButton.setVerticalAlignment(SwingConstants.CENTER);
        renameButton.setHorizontalAlignment(SwingConstants.CENTER);
        renameButton.setHorizontalTextPosition(SwingConstants.CENTER);
        renameButton.setVerticalTextPosition(SwingConstants.CENTER);
        renameButton.setPreferredSize(new Dimension(85, 30));

        newDirButton.setMargin(new Insets(10, 10, 10, 10));
        newDirButton.setVerticalAlignment(SwingConstants.CENTER);
        newDirButton.setHorizontalAlignment(SwingConstants.CENTER);
        newDirButton.setHorizontalTextPosition(SwingConstants.CENTER);
        newDirButton.setVerticalTextPosition(SwingConstants.CENTER);
        newDirButton.setPreferredSize(new Dimension(85, 30));

        aboutButton.setMargin(new Insets(10, 10, 10, 10));
        aboutButton.setVerticalAlignment(SwingConstants.CENTER);
        aboutButton.setHorizontalAlignment(SwingConstants.CENTER);
        aboutButton.setHorizontalTextPosition(SwingConstants.CENTER);
        aboutButton.setVerticalTextPosition(SwingConstants.CENTER);
        aboutButton.setPreferredSize(new Dimension(85, 30));

        panel.add(upButton);
        panel.add(downloadButton);
        panel.add(uploadButton);
        panel.add(removeButton);
        panel.add(renameButton);
        panel.add(newDirButton);
        panel.add(helpButton);
        panel.add(aboutButton);

        // Кнопка "вверх" для навигации по папкам
        upButton.addActionListener(actionEvent -> {
            if (!currentRoot.equals(defaultRoot)) {
                currentRoot = currentRoot.substring(0, currentRoot.lastIndexOf("/"));
                LOGGER.info("Root in storage changed to " + currentRoot);
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
                            "<New dir> - to create new directory in current folder\n" +
                            "<About> - to read about author", "Help",
                    JOptionPane.INFORMATION_MESSAGE);
            LOGGER.info("Help button was used");
        });

        // Обработчик "проваливания" внутрь папки
        tFiles.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                if (tFiles.getSelectedRow() != -1) {
                    Point point = mouseEvent.getPoint();
                    int indexRow = tFiles.rowAtPoint(point);
                    if (mouseEvent.getClickCount() == 2) {
                        currentRoot += "/" + tFiles.getModel().getValueAt(indexRow, columnNames.get("Name"));
                        LOGGER.info("Root in storage changed to " + currentRoot);
                        requestFileTable();
                    }
                }
            }
        });

        // Кнопка скачивания выбранного файла из списка
        downloadButton.addActionListener(actionEvent -> {
            if (tFiles.getSelectedRow() != -1) {
                int indexRow = tFiles.getSelectedRow();
                String fileName = (String) tFiles.getModel().getValueAt(indexRow, columnNames.get("Name"));
                LOGGER.info("Download attempt for file " + currentRoot + "/" + fileName);
                Network.getInstance().sendMessage(new CommandRequest(CommandType.DOWNLOAD,
                        currentRoot + "/" + fileName));
            }
        });

        // Кнопка загрузки файла в хранилище
        uploadButton.addActionListener(actionEvent -> {
            fileChooser.setDialogTitle("Choose folder");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(TablePage.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String fileToUpload = fileChooser.getSelectedFile().getAbsolutePath();
                try {
                    LOGGER.info("Upload attempt for file " + Paths.get(fileToUpload));
                    Network.getInstance().sendMessage(new FileRequest(Paths.get(fileToUpload), currentRoot));
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.error("Error with uploading file " + Paths.get(fileToUpload));
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
                String fileName = (String) tFiles.getModel().getValueAt(indexRow, columnNames.get("Name"));
                String newFileName = JOptionPane.showInputDialog(this,
                        "Input the new name of a file", fileName);
                LOGGER.info("Rename attempt for file " + currentRoot + "/" + fileName +
                        " with new name " + newFileName);
                Network.getInstance().sendMessage(new CommandRequest(CommandType.RENAME,
                        currentRoot + "/" + fileName, currentRoot + "/" + newFileName));
            }
        });

        // Кнопка удаления выбранного файла из списка
        removeButton.addActionListener(actionEvent -> {
            if (tFiles.getSelectedRow() != -1) {
                int indexRow = tFiles.getSelectedRow();
                String fileName = (String) tFiles.getModel().getValueAt(indexRow, columnNames.get("Name"));
                LOGGER.info("Remove attempt for file " + currentRoot + "/" + fileName);
                Network.getInstance().sendMessage(new CommandRequest(CommandType.REMOVE,
                        currentRoot + "/" + fileName));
            }
        });

        // Кнопка создания новой папки
        newDirButton.addActionListener(actionEvent -> {
            String newFolderName = JOptionPane.showInputDialog(this,
                    "Input the name of a new folder",
                    "New folder", JOptionPane.INFORMATION_MESSAGE);
            LOGGER.info("Creating new dir attempt with name " + currentRoot + "/" + newFolderName);
            Network.getInstance().sendMessage(new CommandRequest(CommandType.MKDIR,
                    currentRoot + "/" + newFolderName));
        });

        // Кнопка хелп
        aboutButton.addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(this,
                    "Thanks for using my application. \n" +
                            "Copyright 2020 GB Study Work. All right reserved.\n" +
                            "by Nikita Baliasniy",
                            "About",
                    JOptionPane.INFORMATION_MESSAGE);
            LOGGER.info("About button was used");
        });

        frame.getContentPane().add(BorderLayout.WEST, panel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
        frame.setVisible(true);
        fileChooser = new JFileChooser();
        requestFileTable();
    }


    public void init() {
        new Thread(() -> Network.getInstance().getClientHandler().setCallback(o -> {
            if (o instanceof TableRequest) {
                LOGGER.info("Updating files table for root " + currentRoot);
                TableRequest tableRequest = (TableRequest) o;
                List<FileHandler> fileTable = tableRequest.getFileTable();
                fillFileTable(fileTable);
            }
            if (o instanceof FileRequest) {
                try {
                    LOGGER.info("Downloading a file to local downloads storage");
                    FileRequest fileRequest = (FileRequest) o;
                    Path pathToDownload = Paths.get(downloadsPath + "/" + fileRequest.getFileName());
                    Files.write(pathToDownload, fileRequest.getContent(), StandardOpenOption.CREATE);
                    JOptionPane.showMessageDialog(this,
                            "The file was successfully downloaded to your folder!",
                            "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    LOGGER.info("Download was successfully completed " + fileRequest.getFileName());
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.error("Error with downloading file");
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
                        requestFileTable();
                        LOGGER.info("File was uploaded");
                        JOptionPane.showMessageDialog(this,
                                "The file was successfully uploaded to the cloud storage!",
                                "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("Error with uploading file");
                        JOptionPane.showMessageDialog(this,
                                "There was an error with uploading a file!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (commandRequest.getCommandType() == CommandType.REMOVE) {
                    if (commandRequest.getArg1().equals("OK")) {
                        LOGGER.info("File was removed");
                        JOptionPane.showMessageDialog(this,
                                "The file was successfully removed from storage!",
                                "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        requestFileTable();
                    } else {
                        LOGGER.error("Error with removing file");
                        JOptionPane.showMessageDialog(this,
                                "There was an error with removing a file!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (commandRequest.getCommandType() == CommandType.RENAME) {
                    if (commandRequest.getArg1().equals("OK")) {
                        LOGGER.info("File was renamed");
                        JOptionPane.showMessageDialog(this,
                                "The file was successfully renamed!",
                                "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        requestFileTable();
                    } else {
                        LOGGER.error("Error with renaming file");
                        JOptionPane.showMessageDialog(this,
                                "There was an error with renaming a file!",
                                "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (commandRequest.getCommandType() == CommandType.MKDIR) {
                    if (commandRequest.getArg1().equals("OK")) {
                        LOGGER.info("New folder was created");
                        JOptionPane.showMessageDialog(this,
                                "The folder was successfully created!",
                                "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                        requestFileTable();
                    } else {
                        LOGGER.error("Error with creating new folder");
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
        Network.getInstance().sendMessage(new CommandRequest(CommandType.LIST, currentRoot));
    }

    // Метод отображения списка файлов на сервере
    private void fillFileTable(List<FileHandler> fileTable) {

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (getRowCount() > 0) {
                    Object value = getValueAt(0, column);
                    if (value != null) {
                        return getValueAt(0, column).getClass();
                    }
                }
                return super.getColumnClass(column);
            }
        };
        while (tableModel.getColumnCount() > 0) {
            tableModel.removeRow(0);
        }
        tableModel.addColumn("Icon");
        tableModel.addColumn("Name");
        tableModel.addColumn("Size");
        tableModel.addColumn("Type");

        Image fileImage, folderImage;
        Icon icon;
        try {
            fileImage = ImageIO.read(new File(imgPath + "file.png"));
            folderImage = ImageIO.read(new File(imgPath + "folder.png"));

            if (fileTable.size() > 0) {
                fileTable.stream().filter(FileHandler::isDirectory).forEach(f ->
                        tableModel.addRow(new Object[]{new ImageIcon(folderImage),
                                f.getFileName(), "", "FOLDER"})
                );
                fileTable.stream().filter(f -> !f.isDirectory()).forEach(f ->
                        tableModel.addRow(new Object[]{new ImageIcon(fileImage),
                                f.getFileName(), f.getSize() + " KB", f.getExt()})
                );


            }

            tFiles.setModel(tableModel);
            tFiles.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tFiles.setRowHeight(new ImageIcon(fileImage).getIconHeight());
            tFiles.getColumnModel().getColumn(0).setMinWidth(40);
            tFiles.getColumnModel().getColumn(0).setPreferredWidth(40);
            tFiles.getColumnModel().getColumn(1).setMinWidth(180);
            tFiles.getColumnModel().getColumn(1).setPreferredWidth(180);
            tFiles.getColumnModel().getColumn(2).setMinWidth(80);
            tFiles.getColumnModel().getColumn(2).setPreferredWidth(80);
            tFiles.getColumnModel().getColumn(3).setMinWidth(70);
            tFiles.getColumnModel().getColumn(3).setPreferredWidth(70);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 1; i < columnNames.size(); i++) {
                tFiles.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            tFiles.revalidate();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
