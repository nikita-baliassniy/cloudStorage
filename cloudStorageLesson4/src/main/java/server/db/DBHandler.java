package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class DBHandler {

    private static DBHandler instance;
    private Connection conn;

    private DBHandler() throws SQLException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("db");
        String host = resourceBundle.getString("host");
        String port = resourceBundle.getString("port");
        String db = resourceBundle.getString("db");
        String user = resourceBundle.getString("user");
        String password = resourceBundle.getString("password");

        String jdbcURL = MessageFormat.format(
                "jdbc:mysql://{0}:{1}/{2}", host, port, db);
        conn = DriverManager.getConnection(jdbcURL, user, password);
    }

    public static DBHandler getInstance() {
        if (instance == null) {
            try {
                instance = new DBHandler();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return instance;
    }

    public Connection connection() {
        return conn;
    }

}
