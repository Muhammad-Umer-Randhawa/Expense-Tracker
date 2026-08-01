import java.io.*;
import java.sql.*;
import java.util.Properties;
public class Main{
    public static void main(String[] args){

        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream("../config.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return; // can't continue without config
        }

        String db_url = props.getProperty("db.url");
        String db_user = props.getProperty("db.user");
        String db_password = props.getProperty("db.password"); 

        try (Connection conn = DriverManager.getConnection(db_url, db_user, db_password);) {

            System.out.println("Connected successfully!");

            // String insert = "INSERT INTO categories (name, budget) VALUES (?, ?)";
            // PreparedStatement insertStmt = conn.prepareStatement(insert);

            // insertStmt.setString(1, "Transport");
            // insertStmt.setDouble(2, 3000.00);
            // int row = insertStmt.executeUpdate();
            // System.out.println(row);

            String select = "SELECT * FROM categories";
            PreparedStatement selectStmt = conn.prepareStatement(select);
            ResultSet rs = selectStmt.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double budget = rs.getDouble("budget");
                System.out.println(id + " - " + name + " - " + budget);
            }

            String delete = "DELETE FROM categories WHERE id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(delete);

            deleteStmt.setInt(1,3);
            int row = deleteStmt.executeUpdate();
            System.out.println(row);
            

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}