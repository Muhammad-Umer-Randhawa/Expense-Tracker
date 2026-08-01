package db;

import java.sql.*;
import java.io.*;
import java.util.Properties;

public class DBConnection {
    public Connection connectDatabase() throws SQLException, IOException {

        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        }
        String db_url = props.getProperty("db.url");
        String db_user = props.getProperty("db.user");
        String db_password = props.getProperty("db.password");

        Connection conn = DriverManager.getConnection(db_url, db_user, db_password);
        return conn;
    }
}
