package handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.DBHandler;

public class UserServiceImpl implements UserService {

    private PreparedStatement ps;

    public UserServiceImpl() {
    }

    @Override
    public String getStorage(String login, String password) throws SQLException {
        try {
            System.out.println("IM IN SERVICE");
            ps = DBHandler.getInstance()
                    .connection()
                    .prepareStatement("SELECT storage FROM USERS WHERE " +
                            "login = ?" +
                            " AND password = ?"
                    );
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String s = resultSet.getString(1);
                System.out.println(s);
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
            String storage = "server/" + login;
            ps = DBHandler.getInstance()
                    .connection()
                    .prepareStatement("INSERT INTO USERS (login, password, storage, authorized)" +
                            " VALUES (?, ?, ?, 1)");
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, storage);
            ps.executeUpdate();
            ps.close();
            return "true";
        } catch (SQLException s) {
            s.printStackTrace();
            return "false";
        }
    }

}
