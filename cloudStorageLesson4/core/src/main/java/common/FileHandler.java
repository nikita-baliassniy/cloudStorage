package common;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler implements Serializable {

    private String fileName;
    private long size;
    private boolean isDirectory;
    private String fullPath;

    public FileHandler() {

    }

    public FileHandler(Path path) {
        try {
            this.fileName = path.getFileName().toString();
            this.size = Files.size(path);
            this.isDirectory = Files.isDirectory(path);
            if (this.isDirectory) {
                this.size = 0;
            }
            this.fullPath = path.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file info from path");
        }
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public long getSize() {
        return size;
    }

    public String getFullPath() {
        return fullPath;
    }

}
