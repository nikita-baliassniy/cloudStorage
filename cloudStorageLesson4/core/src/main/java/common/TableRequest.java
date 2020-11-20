package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class TableRequest extends AbstractRequest {

    private List<FileHandler> fileTable;

    public void createTable(Path path) throws IOException {
        fileTable = new ArrayList<>();
        Files.list(path).forEach((p) -> {
            this.fileTable.add(new FileHandler(p));
        });
    }

    public List<FileHandler> getFileTable() {
        return fileTable;
    }

}
