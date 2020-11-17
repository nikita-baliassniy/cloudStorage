package server.handlers;

import java.sql.SQLException;

public interface UserService {

    String getStorage(String login, String password) throws SQLException;

    String addUser(String login, String password);

}
