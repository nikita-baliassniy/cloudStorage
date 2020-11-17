package server.handlers;

import server.db.DBHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceImpl implements UserService {

    private PreparedStatement ps;

    public UserServiceImpl() {
    }

    @Override
    public String getStorage(String login, String password) throws SQLException {
        try {
            ps = DBHandler.getInstance()
                    .connection()
                    .prepareStatement("SELECT storage FROM USERS WHERE login = ? AND password = ?");
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
    public String addUser(String login, String password) {
        try {
            ps = DBHandler.getInstance()
                    .connection()
                    .prepareStatement("INSERT INTO USERS (login, password, storage, authorized) VALUES (?,?,'server', 'true')");
            ps.setString(1, login);
            ps.setString(2, password);
            ps.executeUpdate();
            ps.close();
            return "true";
        } catch (SQLException s) {
            s.printStackTrace();
            return "false";
        }
    }

}
