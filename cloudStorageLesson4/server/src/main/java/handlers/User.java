package handlers;

import java.nio.file.Path;

public class User {

    private String login;
    private String password;
    private Path pathToFiles;
    private boolean authorized;

    public User(String login, String password, Path pathToFiles) {
        this.login = login;
        this.password = password;
        this.pathToFiles = pathToFiles;
        this.authorized = true;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", pathToFiles='" + pathToFiles.toString() + '\'' +
                '}';
    }

    public User() {
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPathToFiles(Path pathToFiles) {
        this.pathToFiles = pathToFiles;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Path getPathToFiles() {
        return pathToFiles;
    }
}
