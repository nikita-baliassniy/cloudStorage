package common;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Обертка для файлов, складируемых в TableRequest
 */
public class FileHandler implements Serializable {

    private String fileName;
    private long size;
    private boolean isDirectory;
    private String fullPath;
    private String ext;

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
            this.ext = fullPath.substring(fullPath.lastIndexOf(".") + 1).toUpperCase();
        } catch (IOException e) {
            e.printStackTrace();
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

    public String getExt() {
        return ext;
    }
}
