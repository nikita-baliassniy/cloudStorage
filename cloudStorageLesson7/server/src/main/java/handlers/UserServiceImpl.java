package handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.DBHandler;

/**
 * Сервис по работе с БД пользователей
 */
public class UserServiceImpl implements UserService {

    private PreparedStatement ps;

    public UserServiceImpl() {
    }

    @Override
    public String getStorage(String login, String password) throws SQLException {
        try {
            ps = DBHandler.getInstance()
                    .connection()
                    .prepareStatement("SELECT storage FROM storage.USERS WHERE " +
                            "login = ?" +
                            " AND password = ?"
                    );
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String s = resultSet.getString(1);
                ps.close();
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ps.close();
        return null;
    }

    @Override
    public boolean addUser(String login, String password) {
        try {
            String storage = "server/" + login;
            ps = DBHandler.getInstance()
                    .connection()
                    .prepareStatement("INSERT INTO storage.USERS (login, password, storage, authorized)" +
                            " VALUES (?, ?, ?, 1)");
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, storage);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException s) {
            s.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isLoginFree(String login) throws SQLException {
        try {
            ps = DBHandler.getInstance()
                    .connection()
                    .prepareStatement("SELECT storage FROM storage.USERS WHERE " +
                            "login = ?"
                    );
            ps.setString(1, login);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String s = resultSet.getString(1);
                ps.close();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ps.close();
        return true;
    }
}
