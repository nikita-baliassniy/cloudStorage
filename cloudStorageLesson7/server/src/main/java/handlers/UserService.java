package handlers;

import java.sql.SQLException;

/**
 * Интерфейс сервиса по работе с БД пользователей
 */
public interface UserService {

    String getStorage(String login, String password) throws SQLException;

    boolean addUser(String login, String password);

}
