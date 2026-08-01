import java.io.*;
import java.sql.*;
import db.DBConnection;
public class Main{
    public static void main(String[] args){

        DBConnection db = new DBConnection();
        try (Connection conn = db.connectDatabase()) {

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
            } catch (IOException e) {
                System.err.println(e.getMessage());
        }
    }
}