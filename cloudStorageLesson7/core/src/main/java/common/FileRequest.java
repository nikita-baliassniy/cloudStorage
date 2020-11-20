package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Упаковка для пересылки файлов между сервером и клиентов
 */
public class FileRequest extends AbstractRequest {

    private String fileName;
    private byte[] content;
    private String pathInCloud;

    public FileRequest(Path path) throws IOException {
        this.fileName = path.getFileName().toString();
        this.content = Files.readAllBytes(path);
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public String getPathInCloud() {
        return pathInCloud;
    }

    public FileRequest(Path path, String pathInCloud) throws IOException {
        this.fileName = path.getFileName().toString();
        this.content = Files.readAllBytes(path);
        this.pathInCloud = pathInCloud;
    }

}